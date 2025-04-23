package com.wire.bots.infrastructure

import com.wire.bots.domain.reminder.Reminder
import com.wire.bots.infrastructure.client.MessagePayload
import com.wire.bots.infrastructure.client.OutgoingMessage
import com.wire.bots.infrastructure.client.OutgoingMessageType
import com.wire.bots.infrastructure.repository.ReminderEntity

fun Reminder.toEntity(): ReminderEntity =
    when (this) {
        is Reminder.RecurringReminder ->
            ReminderEntity(
                conversationId = this.conversationId,
                taskId = this.taskId,
                task = this.task,
                createdAt = this.createdAt,
                scheduledCron = this.scheduledCron,
                isEternal = true,
            )

        is Reminder.SingleReminder ->
            ReminderEntity(
                conversationId = this.conversationId,
                taskId = this.taskId,
                task = this.task,
                scheduledAt = this.scheduledAt,
                createdAt = this.createdAt,
                isEternal = false,
            )
    }

fun ReminderEntity.toDomain(): Reminder {
    return when (isEternal) {
        true -> return Reminder.RecurringReminder(
            conversationId = this.conversationId,
            taskId = this.taskId,
            task = this.task,
            scheduledCron = this.scheduledCron!!,
            createdAt = this.createdAt,
        )

        false ->
            Reminder.SingleReminder(
                conversationId = this.conversationId,
                taskId = this.taskId,
                task = this.task,
                scheduledAt = this.scheduledAt!!,
                createdAt = this.createdAt,
            )
    }
}

fun String.toOutgoingMessage(): OutgoingMessage =
    OutgoingMessage(
        type = OutgoingMessageType.text,
        text = MessagePayload.Text(this),
    )
