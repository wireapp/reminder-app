package com.wire.bots.domain

interface ReminderRepository {

    fun persistReminder(reminder: Reminder)
}