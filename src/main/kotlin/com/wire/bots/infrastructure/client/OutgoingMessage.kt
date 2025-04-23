package com.wire.bots.infrastructure.client

import kotlinx.serialization.Serializable

@Serializable
data class OutgoingMessage(
    val type: OutgoingMessageType,
    val text: MessagePayload.Text? = null,
)

sealed interface MessagePayload {
    @Serializable
    data class Text(
        val data: String,
    ) : MessagePayload
}

enum class OutgoingMessageType {
    Text,
}
