package com.wire.bots.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import com.wire.bots.domain.DomainComponent
import com.wire.bots.domain.PlainConversationId
import com.wire.bots.domain.token.TokenRepository

/**
 * Save token for conversation.
 * Ensures, that we have a token for the conversation, so we can send messages back to wire-server.
 */
@DomainComponent
class SaveToken() {
//    private val tokenRepository: TokenRepository) {
//    fun invoke(conversationId: PlainConversationId, token: String): Either<Throwable, Unit> = either {
//        tokenRepository.insertToken(conversationId, token)
//    }
}