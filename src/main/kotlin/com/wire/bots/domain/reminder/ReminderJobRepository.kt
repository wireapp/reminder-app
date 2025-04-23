package com.wire.bots.domain.reminder

import arrow.core.Either

interface ReminderJobRepository {
    fun scheduleReminderJob(reminder: Reminder): Either<Throwable, ReminderNextSchedule>
}
