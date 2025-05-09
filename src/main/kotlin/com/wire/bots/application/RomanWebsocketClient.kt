package com.wire.bots.application

import com.wire.bots.domain.event.EventProcessor
import com.wire.bots.infrastructure.LenientJson
import jakarta.annotation.PostConstruct
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/*
* Deprecated: This class is not used anymore. Use [MlsSdkClient] instead.
*/
class RomanWebsocketClient(
    @ConfigProperty(name = "quarkus.rest-client.wire-proxy-services-api.url")
    private val baseUrl: String,
    @ConfigProperty(name = "quarkus.rest-client.wire-proxy-services-api.bot-key")
    private val botApiKey: String,
    private val eventProcessor: EventProcessor
) : WebSocket.Listener {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private companion object {
        private const val THREAD_POOL_SIZE = 5
    }

    private val executorService: ExecutorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE)

    private val httpClient: HttpClient =
        HttpClient
            .newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .executor(executorService)
            .build()

    private val wsUri =
        run {
            URI(baseUrl.replace("https://", "wss://"))
                .resolve("/await/" + URLEncoder.encode(botApiKey, "utf-8"))
        }

    @PostConstruct
    fun init() {
        httpClient
            .newWebSocketBuilder()
            .buildAsync(wsUri, this)
            .join()
    }

    override fun onOpen(webSocket: WebSocket?) {
        logger.info("Websocket opened")
        super.onOpen(webSocket)
    }

    override fun onText(
        webSocket: WebSocket?,
        data: CharSequence?,
        last: Boolean
    ): CompletionStage<*> {
        super.onText(webSocket, data, last)
        logger.debug("Message received raw: $data")
        val eventDTO = LenientJson.parser.decodeFromString<EventDTO>(data.toString())
        EventMapper.fromEvent(eventDTO).fold(
            ifLeft = { error ->
                logger.warn("Processing command with error: $error")
                eventProcessor.process(error)
            },
            ifRight = { event ->
                logger.info("Processing command parsed to: $event")
                eventProcessor.process(event)
            }
        )
        return CompletableFuture<Void>()
    }

    override fun onClose(
        webSocket: WebSocket?,
        statusCode: Int,
        reason: String?
    ): CompletionStage<*> {
        super.onClose(webSocket, statusCode, reason)
        logger.info("Websocket close: ${reason ?: "no reason"}, reopening...")
        init()
        return CompletableFuture<Void>().also { it.complete(null) }
    }

    override fun onError(
        webSocket: WebSocket?,
        error: Throwable?
    ) {
        super.onError(webSocket, error)
        logger.info("Websocket error: ${error?.message}, reopening...")
        init()
    }
}
