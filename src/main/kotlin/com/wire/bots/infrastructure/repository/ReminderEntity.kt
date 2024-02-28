package com.wire.bots.infrastructure.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "REMINDERS")
data class ReminderEntity(
    val createdAt: Instant = Instant.now(),
    val conversationId: String,
    val taskId: String,
    val task: String,
    val scheduledAt: Instant,
    val isEternal: Boolean = false
) : PanacheEntity()