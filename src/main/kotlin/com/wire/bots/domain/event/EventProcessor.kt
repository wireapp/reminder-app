package com.wire.bots.domain.event

import arrow.core.Either
import arrow.core.right
import com.wire.bots.domain.DomainComponent
import com.wire.bots.domain.event.handlers.CommandHandler
import com.wire.bots.domain.event.handlers.SignalHandler
import com.wire.bots.domain.message.OutgoingMessageRepository
import com.wire.bots.domain.usecase.SaveReminderSchedule
import org.slf4j.LoggerFactory

/**
 * Process the event/event and acts accordingly, also handles errors
 *
 * For example, if the event is "help", it will send a help message to the conversation, or
 * if the event is unknown, it will do nothing and send an error message to the conversation.
 */
@DomainComponent
class EventProcessor(
    val commandHandler: CommandHandler,
    val signalHandler: SignalHandler,
    val outgoingMessageRepository: OutgoingMessageRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun process(event: Event): Either<Throwable, Unit> =
        when (event) {
            is Command -> commandHandler.onEvent(event)
            is Signal -> signalHandler.onEvent(event)
        }.mapLeft { unhandled ->
            logger.error("Error processing event: $event", unhandled)
            outgoingMessageRepository.sendMessage(
                conversationId = event.conversationId,
                token = event.token,
                messageContent = getErrorMessage(unhandled)
            )
            unhandled
        }

    private fun getErrorMessage(error: Throwable): String {
        return if (error is SaveReminderSchedule.MaxReminderJobsReached) {
            "❌ Maximum numbers of active reminders reached (currently ${error.max}). " +
                "Please delete some reminders first."
        } else {
            "An error occurred while processing the event, please try again later."
        }
    }

    fun process(error: BotError): Either<Throwable, Unit> =
        when (error) {
            is BotError.Unknown -> handleErrorMessage(error)
            is BotError.Skip -> logger.warn("Event skipped, not necessary to handle").right()
            is BotError.ReminderError -> handleErrorMessage(error)
        }.mapLeft { unhandled ->
            logger.error("Fatal! Error while processing error, closing the door from outside", unhandled)
            unhandled
        }

    private fun handleErrorMessage(error: BotError): Either<Throwable, Unit> {
        return outgoingMessageRepository.sendMessage(
            error.conversationId,
            error.token,
            error.reason
        )
    }
}
