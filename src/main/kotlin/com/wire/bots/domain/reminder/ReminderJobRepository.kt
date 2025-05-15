package com.wire.bots.domain.reminder

import arrow.core.Either

interface ReminderJobRepository {
    fun scheduleReminderJob(reminder: Reminder): Either<Throwable, ReminderNextSchedule>
    fun cancelReminderJob(reminderId: String, conversationId: String): Either<Throwable, Unit>
}
