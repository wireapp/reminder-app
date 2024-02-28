package com.wire.bots.domain

interface TokenRepository {
    fun insertToken(conversationId: PlainConversationId, newToken: String)
    fun getToken(conversationId: PlainConversationId): String
    fun deleteToken(conversationId: PlainConversationId)
}