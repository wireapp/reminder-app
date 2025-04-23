package com.wire.bots.application

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class EventDTO(
    val type: EventTypeDTO,
    val botId: String,
    val userId: String? = null,
    val token: String? = null,
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
    // todo: map later or never.
    @Transient val mentions: List<String> = emptyList(),
)
