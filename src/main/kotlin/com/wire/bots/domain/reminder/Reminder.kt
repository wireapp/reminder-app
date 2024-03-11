package com.wire.bots.domain.reminder

import com.wire.bots.domain.PlainConversationId
import com.wire.bots.domain.TaskId
import java.time.Instant

data class Reminder(
    val createdAt: Instant = Instant.now(),
    val conversationId: PlainConversationId,
    val taskId: TaskId,
    val task: String,
    val scheduledAt: Instant,
    val isEternal: Boolean = false
)

// reminder@time -> map from event to reminder -> validate event -> schedule reminder
// scheduler/quartz delegated -> get reminder info (ie, token or reminder from db?) and execute send reminder.
