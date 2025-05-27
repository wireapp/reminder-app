package com.wire.bots.domain.message

import arrow.core.Either
import com.wire.integrations.jvm.model.QualifiedId

interface OutgoingMessageRepository {
    fun sendMessage(
        conversationId: QualifiedId,
        messageContent: String
    ): Either<Throwable, Unit>
}
