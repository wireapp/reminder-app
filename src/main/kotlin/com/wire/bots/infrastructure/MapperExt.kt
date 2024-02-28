package com.wire.bots.infrastructure

import com.wire.bots.domain.Reminder
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