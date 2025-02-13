package com.wire.bots.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.mdimension.jchronic.Chronic
import com.mdimension.jchronic.Options
import com.mdimension.jchronic.tags.Pointer
import com.wire.bots.domain.event.BotError
import com.wire.bots.domain.event.Command
import com.wire.bots.domain.event.Event
import com.wire.bots.domain.reminder.Reminder
import io.github.yamilmedina.kron.NaturalKronParser
import java.time.Instant
import java.util.*

class ReminderMapper {

    companion object {
        private val INVALID_TIME_TOKENS = listOf("hour", "minute", "second")
        private val VALID_RECURRENT_TOKENS = listOf("every") // todo: expand later, eg. each, daily, weekly, etc.
        private val naturalKronParser = NaturalKronParser()

        fun parseReminder(
            conversationId: String,
            token: String,
            task: String,
            schedule: String
        ): Either<BotError, Event> {
            return when {
                VALID_RECURRENT_TOKENS.any { schedule.contains(it) } && INVALID_TIME_TOKENS.any { schedule.contains(it) } -> {
                    BotError.ReminderError(conversationId, token, BotError.ErrorType.INCREMENT_IN_TIMEUNIT).left()
                }

                VALID_RECURRENT_TOKENS.any { schedule.contains(it) } -> {
                    parseRecurrentTask(conversationId, token, task, schedule)
                }

                else -> parseSingleTask(schedule, conversationId, token, task)
            }
        }

        private fun parseSingleTask(
            schedule: String,
            conversationId: String,
            token: String,
            task: String
        ): Either<BotError.ReminderError, Command.NewReminder> {
            return runCatching {
                val parsedSchedule = Chronic.parse(schedule, Options(Pointer.PointerType.FUTURE))
                val parsedDate = parsedSchedule.beginCalendar.toInstant()
                if (parsedDate.isBefore(Instant.now())) {
                    return BotError.ReminderError(conversationId, token, BotError.ErrorType.DATE_IN_PAST).left()
                }
                Command.NewReminder(
                    conversationId, token,
                    Reminder.SingleReminder(
                        conversationId = conversationId,
                        taskId = UUID.randomUUID().toString(),
                        task = task,
                        scheduledAt = parsedDate
                    )
                ).right()
            }.getOrElse {
                BotError.ReminderError(conversationId, token, BotError.ErrorType.PARSE_ERROR).left()
            }
        }

        private fun parseRecurrentTask(
            conversationId: String,
            token: String,
            task: String,
            schedule: String
        ): Either<BotError.ReminderError, Command.NewReminder> {
            return runCatching {
                Command.NewReminder(
                    conversationId, token,
                    Reminder.RecurringReminder(
                        conversationId = conversationId,
                        taskId = UUID.randomUUID().toString(),
                        task = task,
                        scheduledCron = naturalKronParser.parse(schedule)
                    )
                ).right()
            }.getOrElse {
                BotError.ReminderError(conversationId, token, BotError.ErrorType.PARSE_ERROR).left()
            }
        }
    }

}
