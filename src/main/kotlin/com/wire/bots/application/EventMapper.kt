package com.wire.bots.application

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.wire.bots.domain.event.BotError
import com.wire.bots.domain.event.Command
import com.wire.integrations.jvm.model.QualifiedId

object EventMapper {
    /**
     * Maps the [EventDTO] to a [Command] object so it can be processed by the application.
     */
    fun fromEvent(eventDTO: EventDTO): Either<BotError, Command> =
        runCatching {
            when (eventDTO.type) {
                EventTypeDTO.NEW_TEXT -> {
                    parseCommand(
                        conversationId = eventDTO.conversationId,
                        rawCommand = eventDTO.text?.data.orEmpty()
                    )
                }
                else -> BotError.Skip.left()
            }
        }.getOrElse {
            BotError
                .ReminderError(
                    conversationId = eventDTO.conversationId,
                    errorType = BotError.ErrorType.PARSE_ERROR
                ).left()
        }

    /**
     * Parses the raw event string, and returns a [Command] object.
     */
    private fun parseCommand(
        conversationId: QualifiedId,
        rawCommand: String
    ): Either<BotError, Command> =
        either {
            val words = rawCommand.split(COMMAND_EXPRESSION)
            return when (words[0]) {
                "/help" -> Command.LegacyHelp(conversationId).right()
                "/remind" ->
                    parseCommandArgs(
                        conversationId = conversationId,
                        args = rawCommand.substringAfter("/remind").trimStart()
                    )
                else -> BotError.Skip.left()
            }
        }

    private fun parseCommandArgs(
        conversationId: QualifiedId,
        args: String
    ): Either<BotError, Command> =
        when {
            args.trim() == "help" -> Command.Help(conversationId).right()
            args.trim() == "list" -> Command.ListReminders(conversationId).right()
            args.startsWith("to") -> parseToCommand(conversationId, args)
            args.startsWith("delete") -> parseDeleteCommand(conversationId, args)
            else ->
                BotError
                    .Unknown(
                        conversationId = conversationId,
                        reason = COMMAND_HINT
                    ).left()
        }

    private fun parseToCommand(
        conversationId: QualifiedId,
        args: String
    ): Either<BotError, Command> {
        val reminderArgs = args
            .substringAfter("to")
            .split('"', '“')
            .filter { it.isNotBlank() }
        return if (reminderArgs.size != 2) {
            BotError
                .Unknown(
                    conversationId = conversationId,
                    reason = COMMAND_HINT
                ).left()
        } else {
            val (task, schedule) = reminderArgs
            ReminderMapper
                .parseReminder(
                    conversationId = conversationId,
                    task = task,
                    schedule = schedule
                ).mapLeft { error ->
                    when (error) {
                        is BotError.ReminderError -> error
                        else -> BotError.Unknown(
                            conversationId = conversationId,
                            reason = COMMAND_HINT
                        )
                    }
                }
        }
    }

    private fun parseDeleteCommand(
        conversationId: QualifiedId,
        args: String
    ): Either<BotError, Command> {
        val reminderId = args.substringAfter("delete").trim()
        return if (reminderId.isBlank()) {
            BotError
                .Unknown(
                    conversationId = conversationId,
                    reason = "Please provide a reminder ID to delete."
                ).left()
        } else {
            Command
                .DeleteReminder(
                    conversationId = conversationId,
                    reminderId = reminderId
                ).right()
        }
    }
}

internal val COMMAND_EXPRESSION: Regex = "\\s+".toRegex()
internal val COMMAND_HINT =
    """
    Unknown command, valid options are:
    ```
    > /remind help
    > /remind list
    > /remind to "what" "when"
    > /remind delete <reminderId>
    ```
    """.trimIndent()
