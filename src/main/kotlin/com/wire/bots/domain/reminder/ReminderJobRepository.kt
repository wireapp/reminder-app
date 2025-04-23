package com.wire.bots.domain.reminder

import arrow.core.Either
// import java.util.*

interface ReminderJobRepository {
    fun scheduleReminderJob(reminder: Reminder): Either<Throwable, ReminderNextSchedule>
}
