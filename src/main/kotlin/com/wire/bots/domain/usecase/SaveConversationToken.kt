package com.wire.bots.domain.usecase

import arrow.core.Either
import com.wire.bots.domain.DomainComponent
import com.wire.bots.domain.PlainConversationId
import com.wire.bots.domain.token.TokenRepository

/**
 * Save token for conversation. This is used when bot is added to the conversation.
 * Ensures, that we have a token for the conversation, so we can send messages back to wire-server.
 */
@DomainComponent
class SaveConversationToken(private val tokenRepository: TokenRepository) {
    operator fun invoke(conversationId: PlainConversationId, token: String): Either<Throwable, Unit> =
        tokenRepository.insertToken(conversationId, token)

}