package com.wire.bots.domain.token

import arrow.core.Either
import com.wire.bots.domain.PlainConversationId

interface TokenRepository {
    fun insertToken(conversationId: PlainConversationId, newToken: String): Either<Throwable, Unit>
    fun getToken(conversationId: PlainConversationId): Either<Throwable, String>
    fun deleteToken(conversationId: PlainConversationId): Either<Throwable, Unit>
}
