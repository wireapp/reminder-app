package com.wire.bots.application

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.wire.bots.domain.event.BotError
import com.wire.bots.domain.event.Command
import com.wire.bots.domain.event.Event
import com.wire.bots.domain.event.Signal

object EventMapper {
    /**
     * Maps the [EventDTO] to an [Event] either [Command] object or [Signal], so it can be processed by the application.
     */
    fun fromEvent(eventDTO: EventDTO): Either<BotError, Event> =
        runCatching {
            when (eventDTO.type) {
                EventTypeDTO.NEW_TEXT -> {
                    parseCommand(
                        conversationId = eventDTO.conversationId.orEmpty(),
                        token = eventDTO.token ?: error("Token is null"),
                        rawCommand = eventDTO.text?.data.orEmpty()
                    )
                }

                EventTypeDTO.BOT_REMOVED ->
                    Signal
                        .BotRemoved(
                            conversationId = eventDTO.conversationId.orEmpty(),
                            token = eventDTO.token.orEmpty()
                        ).right()

                EventTypeDTO.BOT_REQUEST ->
                    Signal
                        .BotAdded(
                            conversationId = eventDTO.conversationId.orEmpty(),
                            token = eventDTO.token ?: error("Token is null")
                        ).right()

                else -> BotError.Skip.left()
            }
        }.getOrElse {
            BotError
                .ReminderError(
                    conversationId = eventDTO.conversationId.orEmpty(),
                    token = eventDTO.token.orEmpty(),
                    errorType = BotError.ErrorType.PARSE_ERROR
                ).left()
        }

    /**
     * Parses the raw event string, and returns a [Command] object.
     */
    private fun parseCommand(
        conversationId: String,
        token: String,
        rawCommand: String
    ): Either<BotError, Event> =
        either {
            val words = rawCommand.split(COMMAND_EXPRESSION)
            return when (words[0]) {
                "/help" -> Command.LegacyHelp(conversationId, token).right()
                "/remind" ->
                    parseCommandArgs(
                        conversationId = conversationId,
                        token = token,
                        args = rawCommand.substringAfter("/remind").trimStart()
                    )

                else -> BotError.Skip.left()
            }
        }

    private fun parseCommandArgs(
        conversationId: String,
        token: String,
        args: String
    ): Either<BotError, Event> =
        when {
            args.trim() == "help" -> Command.Help(conversationId, token).right()
            args.trim() == "list" -> Command.ListReminders(conversationId, token).right()
            args.startsWith("to") -> parseToCommand(conversationId, token, args)
            args.startsWith("delete") -> parseDeleteCommand(conversationId, token, args)
            else ->
                BotError
                    .Unknown(
                        conversationId = conversationId,
                        token = token,
                        reason = COMMAND_HINT
                    ).left()
        }

    private fun parseToCommand(
        conversationId: String,
        token: String,
        args: String
    ): Either<BotError, Event> {
        val reminderArgs = args
            .substringAfter("to")
            .split('\"', '“')
            .filter { it.isNotBlank() }
        return if (reminderArgs.size != 2) {
            BotError
                .Unknown(
                    conversationId = conversationId,
                    token = token,
                    reason = COMMAND_HINT
                ).left()
        } else {
            val (task, schedule) = reminderArgs
            ReminderMapper
                .parseReminder(
                    conversationId = conversationId,
                    token = token,
                    task = task,
                    schedule = schedule
                ).mapLeft { error ->
                    when (error) {
                        is BotError.ReminderError -> error
                        else -> BotError.Unknown(
                            conversationId = conversationId,
                            token = token,
                            reason = COMMAND_HINT
                        )
                    }
                }
        }
    }

    private fun parseDeleteCommand(
        conversationId: String,
        token: String,
        args: String
    ): Either<BotError, Event> {
        val reminderId = args.substringAfter("delete").trim()
        return if (reminderId.isBlank()) {
            BotError
                .Unknown(
                    conversationId = conversationId,
                    token = token,
                    reason = "Please provide a reminder ID to delete."
                ).left()
        } else {
            Command
                .DeleteReminder(
                    conversationId = conversationId,
                    token = token,
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
