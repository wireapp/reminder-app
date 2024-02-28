package com.wire.bots.infrastructure.repository

import arrow.core.Either
import arrow.core.raise.either
import com.wire.bots.domain.OutgoingMessageRepository
import com.wire.bots.domain.PlainConversationId

class DefaultOutgoingMessageRepository : OutgoingMessageRepository {
    override fun sendTextMessage(
        conversationId: PlainConversationId, messageContent: String
    ): Either<Throwable, Unit> = either {
        // todo send text message back to the conversation
    }
}

