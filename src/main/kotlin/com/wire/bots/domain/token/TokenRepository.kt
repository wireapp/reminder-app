package com.wire.bots.domain.token

import com.wire.bots.domain.PlainConversationId

interface TokenRepository {
    fun insertToken(conversationId: PlainConversationId, newToken: String)
    fun getToken(conversationId: PlainConversationId): String
    fun deleteToken(conversationId: PlainConversationId)
}