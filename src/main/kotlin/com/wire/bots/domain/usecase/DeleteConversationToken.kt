package com.wire.bots.domain.usecase

import arrow.core.Either
import com.wire.bots.domain.DomainComponent
import com.wire.bots.domain.PlainConversationId
import com.wire.bots.domain.token.TokenRepository

/**
 * Delete token for conversation, this is used when bot is removed from the conversation.
 * Ensures, that we have a token for the conversation, so we can send messages back to wire-server.
 */
@DomainComponent
class DeleteConversationToken(
    private val tokenRepository: TokenRepository
) {
    @Suppress("ktlint:standard:function-signature")
    operator fun invoke(conversationId: PlainConversationId): Either<Throwable, Unit> =
        tokenRepository.deleteToken(conversationId)
}
