package com.wire.bots.application

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.mdimension.jchronic.Chronic
import com.mdimension.jchronic.Options
import com.mdimension.jchronic.tags.Pointer
import com.wire.bots.domain.event.BotError
import com.wire.bots.domain.event.Command
import com.wire.bots.domain.event.Event
import com.wire.bots.domain.reminder.Reminder
import io.github.yamilmedina.naturalkron.NaturalKronExpressionParser
import java.time.Instant
import java.util.*

class ReminderMapper {

    companion object {
        private val INVALID_TIME_TOKENS = listOf("hour", "minute", "second")
        private val VALID_RECURRENT_TOKENS = listOf("every", "each", "daily", "weekly", "monthly", "yearly")
        private val JCHRONIC_OPTS = Options(Pointer.PointerType.FUTURE)

        fun parseReminder(
            conversationId: String,
            token: String,
            task: String,
            schedule: String
        ): Either<BotError, Event> = either {
            if (VALID_RECURRENT_TOKENS.any { schedule.contains(it) } && INVALID_TIME_TOKENS.any { schedule.contains(it) }) {
                return BotError.ReminderError(conversationId, token, BotError.ErrorType.INCREMENT_IN_TIMEUNIT).left()
            }

            if (VALID_RECURRENT_TOKENS.any { schedule.contains(it) }) {
                return runCatching {
                    Command.NewReminder(
                        conversationId, token,
                        Reminder.RecurringReminder(
                            conversationId = conversationId,
                            taskId = UUID.randomUUID().toString(),
                            task = task,
                            scheduledCron = NaturalKronExpressionParser().parse(schedule).toString()
                        )
                    ).right()
                }.getOrElse {
                    BotError.ReminderError(conversationId, token, BotError.ErrorType.PARSE_ERROR).left()
                }
            }

            return runCatching {
                val parsedSchedule = Chronic.parse(schedule, JCHRONIC_OPTS)
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
    }

}
