package com.wire.bots.domain.reminder

import com.wire.integrations.jvm.model.QualifiedId
import com.wire.bots.domain.TaskId
import java.time.Instant
import java.util.Date

sealed interface Reminder {
    val createdAt: Instant
//    val conversationId: PlainConversationId
    val conversationId: QualifiedId
    val taskId: TaskId
    val task: String

    data class SingleReminder(
        override val createdAt: Instant = Instant.now(),
//        override val conversationId: PlainConversationId,
        override val conversationId: QualifiedId,
        override val taskId: TaskId,
        override val task: String,
        val scheduledAt: Instant
    ) : Reminder

    data class RecurringReminder(
        override val createdAt: Instant = Instant.now(),
//        override val conversationId: PlainConversationId,
        override val taskId: TaskId,
        override val conversationId: QualifiedId,
        override val task: String,
        val scheduledCron: String
    ) : Reminder
}

class ReminderNextSchedule(
    val reminder: Reminder,
    val nextSchedules: List<Date>
)
