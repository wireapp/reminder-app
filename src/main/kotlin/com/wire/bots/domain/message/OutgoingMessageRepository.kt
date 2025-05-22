package com.wire.bots.domain.message

import arrow.core.Either
//import com.wire.bots.domain.PlainConversationId
import com.wire.integrations.jvm.model.QualifiedId

interface OutgoingMessageRepository {
    fun sendMessage(
//        conversationId: PlainConversationId,
        conversationId: QualifiedId,
        messageContent: String
    ): Either<Throwable, Unit>
}
