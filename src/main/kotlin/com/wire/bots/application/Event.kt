package com.wire.bots.application

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Event(
    val type: EventType,
    val botId: String,
    val userId: String,
    val token: String,
    val conversationId: String? = null,
    val text: TextContent? = null,
    val handle: String? = null,
    val locale: String? = null,
    val conversation: String? = null,
    val messageId: String? = null,
    val refMessageId: String? = null,
    val emoji: String? = null,
)

@Serializable
data class TextContent(
    val data: String,
    @Transient val mentions: List<String> = emptyList() // todo: map later or never.
)