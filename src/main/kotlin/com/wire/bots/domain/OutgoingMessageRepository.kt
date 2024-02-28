package com.wire.bots.domain

import arrow.core.Either

interface OutgoingMessageRepository {
    fun sendTextMessage(conversationId: PlainConversationId, messageContent: String): Either<Throwable, Unit>
}