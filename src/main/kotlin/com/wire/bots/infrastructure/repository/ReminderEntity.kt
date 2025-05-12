package com.wire.bots.infrastructure.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "REMINDERS")
data class ReminderEntity(
    @Column(name = "created_at") val createdAt: Instant = Instant.now(),
    @Column(name = "conversation_id") val conversationId: String,
    @Column(name = "task_id") val taskId: String,
    @Column(name = "task") val task: String,
    @Column(name = "scheduled_at") val scheduledAt: Instant? = null,
    @Column(name = "scheduled_cron") val scheduledCron: String? = null,
    @Column(name = "is_eternal") val isEternal: Boolean = false
) : PanacheEntity()
