package com.wire.bots.domain

import java.time.Instant

data class Reminder(
    val createdAt: Instant = Instant.now(),
    val conversationId: PlainConversationId,
    val taskId: TaskId,
    val task: String,
    val scheduledAt: Instant,
    val isEternal: Boolean = false
)

// reminder@time -> map from command to reminder -> validate command -> schedule reminder
// scheduler/quartz delegated -> get reminder info (ie, token or reminder from db?) and execute send reminder.
