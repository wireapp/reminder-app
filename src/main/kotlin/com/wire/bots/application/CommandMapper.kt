package com.wire.bots.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.right
import com.wire.bots.domain.command.Command

object CommandMapper {
    /**
     * Maps the [Event] to a [Command] object, so it can be processed by the application.
     */
    fun fromEvent(event: Event): Command {
        return when (event.type) {
            EventType.NEW_TEXT -> {
                parseCommand(
                    conversationId = event.conversationId.orEmpty(),
                    token = event.token,
                    rawCommand = event.text?.data.orEmpty()
                ).fold(
                    ifLeft = {
                        Command.UnknownCommand(
                            conversationId = event.conversationId.orEmpty(),
                            token = event.token,
                            reason = "Error while parsing command $it"
                        )
                    },
                    ifRight = { it }
                )
            }

            EventType.BOT_REQUEST -> TODO("Implement token persistence")

            else -> Command.IrrelevantCommand
        }
    }

    /**
     * Parses the raw command string, and returns a [Command] object.
     */
    private fun parseCommand(
        conversationId: String,
        token: String,
        rawCommand: String,
    ): Either<Error, Command> = either {
        val words = rawCommand.split(COMMAND_EXPRESSION)
        if (!isAbleToParseCommand(words)) return Command.UnknownCommand(conversationId, token).right()
        return when (words[0]) {
            "/help" -> {
                Command.LegacyHelp(conversationId, token).right()
            }

            "/remind.help" -> {
                Command.Help(conversationId, token).right()
            }

            "/remind.list" -> {
                Command.UnknownCommand(conversationId, "Not implemented").right()
            }

            "/remind.new" -> {
                Command.UnknownCommand(conversationId, "Not implemented").right()
            }

            "/remind.delete" -> {
                Command.UnknownCommand(conversationId, "Not implemented").right()
            }

            else -> Command.UnknownCommand(conversationId, token).right()
        }
    }

    /**
     * Checks if the command can be parsed, based on the expected min and max number of words this bot can handle.
     */
    private fun isAbleToParseCommand(words: List<String>) =
        words.isNotEmpty() && words.size in 1..3
}

internal val COMMAND_EXPRESSION: Regex = "\\s+".toRegex()
