package com.wire.bots.infrastructure

import com.wire.bots.domain.reminder.Reminder
import com.wire.bots.infrastructure.client.MessagePayload
import com.wire.bots.infrastructure.client.OutgoingMessage
import com.wire.bots.infrastructure.client.OutgoingMessageType
import com.wire.bots.infrastructure.repository.ReminderEntity

fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        conversationId = this.conversationId,
        taskId = this.taskId,
        task = this.task,
        scheduledAt = this.scheduledAt,
        createdAt = this.createdAt,
        isEternal = this.isEternal
    )
}

fun ReminderEntity.toDomain(): Reminder {
    return Reminder(
        conversationId = this.conversationId,
        taskId = this.taskId,
        task = this.task,
        scheduledAt = this.scheduledAt,
        createdAt = this.createdAt,
        isEternal = this.isEternal
    )
}

fun String.toOutgoingMessage(): OutgoingMessage {
    return OutgoingMessage(
        type = OutgoingMessageType.text,
        text = MessagePayload.Text(this),
    )
}
