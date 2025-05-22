package com.wire.bots.infrastructure.repository

import arrow.core.Either
import arrow.core.raise.either
import com.wire.bots.application.MlsSdkClient
import com.wire.bots.domain.PlainConversationId
import com.wire.bots.domain.message.OutgoingMessageRepository
import com.wire.integrations.jvm.model.QualifiedId
import com.wire.integrations.jvm.model.WireMessage
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class MlsSdkOutgoingMessageRepository(
    val conversationRemoteApi: MlsSdkClient
) : OutgoingMessageRepository {
    override fun sendMessage(
        conversationId: QualifiedId,
        messageContent: String
    ): Either<Throwable, Unit> =
        either {
//            val splitConversation = conversationId.split("@")
//            val uuid = UUID.fromString(splitConversation[0])
//            val domain = splitConversation[1]
//            val qualifiedID = QualifiedId(uuid, domain)
            val manager = conversationRemoteApi.getManager()
            val message = WireMessage.Text.create(
                conversationId = conversationId,
                text = messageContent
            )
            manager.sendMessage(
                message = message
            )
        }
}
