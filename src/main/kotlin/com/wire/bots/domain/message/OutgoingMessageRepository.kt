package com.wire.bots.domain.message

import arrow.core.Either
import com.wire.bots.domain.PlainConversationId

interface OutgoingMessageRepository {
    fun sendMessage(
        conversationId: PlainConversationId,
        messageContent: String
    ): Either<Throwable, Unit>
}
