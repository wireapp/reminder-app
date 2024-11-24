package com.wire.bots.infrastructure.repository

import arrow.core.Either
import arrow.core.raise.either
import com.wire.bots.domain.PlainConversationId
import com.wire.bots.domain.token.TokenRepository
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class DefaultTokenRepository : PanacheRepository<TokenEntity>, TokenRepository {

    @Transactional
    override fun insertToken(conversationId: PlainConversationId, newToken: String): Either<Throwable, Unit> = either {
        persist(TokenEntity(conversationId, newToken))
    }

    override fun getToken(conversationId: PlainConversationId): Either<Throwable, String> = either {
        find("conversationId", sort = Sort.by("createdAt", Sort.Direction.Descending), conversationId)
            .list().first().token
    }

    @Transactional
    override fun deleteToken(conversationId: PlainConversationId): Either<Throwable, Unit> = either {
        delete("conversationId", conversationId)
    }
}