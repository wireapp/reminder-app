/*
 * Wire
 * Copyright (C) 2025 Wire Swiss GmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */

package com.wire.bots.application

import arrow.core.Either
import com.wire.bots.domain.event.BotError
import com.wire.bots.domain.event.Event
import com.wire.bots.domain.event.EventProcessor
// import com.wire.bots.infrastructure.repository.MlsSdkOutgoingMessageRepository
import com.wire.integrations.jvm.WireAppSdk
import com.wire.integrations.jvm.WireEventsHandler
import com.wire.integrations.jvm.model.WireMessage
import com.wire.integrations.jvm.service.WireApplicationManager
import com.wire.integrations.jvm.utils.obfuscateId
import io.quarkus.runtime.Startup
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.LoggerFactory
import java.util.UUID

private val logger = LoggerFactory.getLogger("RemindAppMlsSdk")

/**
 * MlsSdkClient is the entry point for Wire Apps SDK integration.
 * It handles the connection to Wire and processes messages using reminder logic.
 */

@ApplicationScoped
@Startup
class MlsSdkClient(
    private val eventProcessor: EventProcessor,
//    private val mlsSdkOutgoingMessageRepository: MlsSdkOutgoingMessageRepository

    @ConfigProperty(name = "quarkus.rest-client.wire-proxy-services-api.bot-key")
    private val apiToken: String,
    @ConfigProperty(name = "quarkus.rest-client.wire-proxy-services-api.url")
    private val apiHost: String
) {
    private var manager: WireApplicationManager? = null
    fun getManager(): WireApplicationManager? {
        return manager
    }
    @PostConstruct
    fun init() {
        val wireAppSdk =
            WireAppSdk(
                applicationId = UUID.randomUUID(),
                apiToken = apiToken,
                apiHost = apiHost,
                cryptographyStoragePassword = "myDummyPassword",
                object : WireEventsHandler() {
                    override suspend fun onNewMessageSuspending(wireMessage: WireMessage.Text) {
                        logger.info("Received Text Message: $wireMessage")
                        // Create an EventDTO from the WireMessage
                        val eventDTO = EventDTO(
                            type = EventTypeDTO.NEW_TEXT,
                            botId = "",
                            userId = wireMessage.sender?.id.toString(),
                            token = wireMessage.conversationId.toString(),
                            conversationId = "${wireMessage.conversationId.id}@${wireMessage.conversationId.domain}",
                            text = wireMessage.text?.let { TextContent(it) }
                        )
                        // Process the event using the reminder bot logic
                        processEvent(eventDTO)
                    }

                    override suspend fun onNewAssetSuspending(wireMessage: WireMessage.Asset) {
                        logger.info("Received Asset Message: $wireMessage")
                        // Assets are not handled by reminder bot
                    }

                    override suspend fun onNewCompositeSuspending(
                        wireMessage: WireMessage.Composite
                    ) {
                        logger.info("Received Composite Message: $wireMessage")
                        // Composite messages are not handled by reminder bot
                    }

                    override suspend fun onNewButtonActionSuspending(
                        wireMessage: WireMessage.ButtonAction
                    ) {
                        logger.info("Received ButtonAction Message: $wireMessage")
                        // Button actions are not handled by reminder bot
                    }

                    override suspend fun onNewButtonActionConfirmationSuspending(
                        wireMessage: WireMessage.ButtonActionConfirmation
                    ) {
                        logger.info("Received ButtonActionConfirmation Message: $wireMessage")
                        // Button action confirmations are not handled by reminder bot
                    }
                }
            )

        logger.info("Starting Wire Apps SDK...")
        wireAppSdk.startListening()
        val applicationManager = wireAppSdk.getApplicationManager()
        manager = applicationManager
//        // Give the application manager to our custom repository
//        mlsSdkOutgoingMessageRepository.setApplicationManager(applicationManager)

        applicationManager.getStoredTeams().forEach {
            logger.info("Team: $it")
        }
        applicationManager.getStoredConversations().forEach {
            logger.info("Conversation: $it")
        }
        logger.info("Wire backend domain: ${applicationManager.getBackendConfiguration().domain}")

        // Use wireAppSdk.stop() to stop the SDK or just stop it with Ctrl+C/Cmd+C
    }

    /**
     * Process an event using the reminder bot logic
     */
    private fun processEvent(eventDTO: EventDTO) {
        try {
            logger.debug("Processing event: $eventDTO")
            val eventResult: Either<BotError, Event> = EventMapper.fromEvent(eventDTO)
            eventResult.fold(
                ifLeft = { error ->
                    logger.warn("Processing event with error: $error")
                    eventProcessor.process(error)
                },
                ifRight = { event ->
                    logger.info("Processing event parsed to: $event")
                    eventProcessor.process(event)
                }
            )
        } catch (e: IllegalArgumentException) {
            logger.error("Error processing event", e)
        }
    }

    @Produces
    @ApplicationScoped
    fun provideApplicationManager(): WireApplicationManager {
        return manager ?: throw IllegalStateException("Manager not initialized yet")
    }
}
