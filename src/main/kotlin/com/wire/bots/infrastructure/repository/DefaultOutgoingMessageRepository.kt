package com.wire.bots.infrastructure.repository

import arrow.core.Either
import arrow.core.raise.either
import com.wire.bots.domain.PlainConversationId
import com.wire.bots.domain.message.OutgoingMessageRepository
import com.wire.bots.infrastructure.client.ConversationRemoteApi
import com.wire.bots.infrastructure.toOutgoingMessage
import org.eclipse.microprofile.rest.client.inject.RestClient

@Deprecated("This class is not used anymore", ReplaceWith("MlsSdkOutgoingMessageRepository"))
class DefaultOutgoingMessageRepository(
    @RestClient val conversationRemoteApi: ConversationRemoteApi
) : OutgoingMessageRepository {
    override fun sendMessage(
        conversationId: PlainConversationId,
        token: String,
        messageContent: String
    ): Either<Throwable, Unit> =
        either {
            conversationRemoteApi.sendMessage(token, messageContent.toOutgoingMessage())
        }
}
