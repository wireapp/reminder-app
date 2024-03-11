package com.wire.bots.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.right
import com.wire.bots.domain.event.Command
import com.wire.bots.domain.event.Error
import com.wire.bots.domain.event.Event
import com.wire.bots.domain.event.Signal

object EventMapper {
    /**
     * Maps the [EventDTO] to an [Event] either [Command] object or [Signal], so it can be processed by the application.
     */
    fun fromEvent(eventDTO: EventDTO): Event {
        return when (eventDTO.type) {
            EventTypeDTO.NEW_TEXT -> {
                parseCommand(
                    conversationId = eventDTO.conversationId.orEmpty(),
                    token = eventDTO.token!!,
                    rawCommand = eventDTO.text?.data.orEmpty()
                ).fold(
                    ifLeft = {
                        Error.Unknown(
                            conversationId = eventDTO.conversationId.orEmpty(),
                            token = eventDTO.token,
                            reason = "Error while parsing event: $it"
                        )
                    },
                    ifRight = { it }
                )
            }

            EventTypeDTO.BOT_REMOVED -> Signal.BotRemoved(
                conversationId = eventDTO.conversationId.orEmpty(),
                token = eventDTO.token.orEmpty()
            )

            EventTypeDTO.BOT_REQUEST -> Signal.BotAdded(
                conversationId = eventDTO.conversationId.orEmpty(),
                token = eventDTO.token!!
            )

            else -> Error.Skip
        }
    }

    /**
     * Parses the raw event string, and returns a [Command] object.
     */
    private fun parseCommand(
        conversationId: String,
        token: String,
        rawCommand: String,
    ): Either<Error, Event> = either {
        val words = rawCommand.split(COMMAND_EXPRESSION)
        if (!isAbleToParseCommand(words)) return Error.Skip.right()
        return when (words[0]) {
            "/help" -> {
                Command.LegacyHelp(conversationId, token).right()
            }

            "/remind.help" -> {
                Command.Help(conversationId, token).right()
            }

            "/remind.list" -> {
                Error.Unknown(conversationId, token, "Not implemented").right()
            }

            "/remind.new" -> {
                Error.Unknown(conversationId, token, "Not implemented").right()
            }

            "/remind.delete" -> {
                Error.Unknown(conversationId, token, "Not implemented").right()
            }

            else -> Error.Skip.right()
        }
    }

    /**
     * Checks if the event can be parsed, based on the expected min and max number of words this bot can handle.
     */
    private fun isAbleToParseCommand(words: List<String>) =
        words.isNotEmpty() && words.size in 1..3

}

internal val COMMAND_EXPRESSION: Regex = "\\s+".toRegex()
