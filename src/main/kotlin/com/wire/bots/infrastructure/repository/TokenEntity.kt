package com.wire.bots.infrastructure.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "TOKENS")
data class TokenEntity(
    @Column(name = "conversation_id") val conversationId: String,
    @Column(name = "token") val token: String,
    @Column(name = "created_at") val createdAt: Instant = Instant.now()
) : PanacheEntity()
