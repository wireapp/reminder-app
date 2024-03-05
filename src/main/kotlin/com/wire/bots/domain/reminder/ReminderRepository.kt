package com.wire.bots.domain.reminder

interface ReminderRepository {

    fun persistReminder(reminder: Reminder)
}