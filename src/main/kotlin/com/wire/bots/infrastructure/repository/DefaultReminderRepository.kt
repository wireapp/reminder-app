package com.wire.bots.infrastructure.repository

import arrow.core.Either
import com.wire.bots.domain.PlainConversationId
import com.wire.bots.domain.reminder.Reminder
import com.wire.bots.domain.reminder.ReminderRepository
import com.wire.bots.infrastructure.toEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class DefaultReminderRepository : PanacheRepository<ReminderEntity>, ReminderRepository {

    @Transactional
    override fun persistReminder(reminder: Reminder): Either<Throwable, Unit> = Either.catch {
        persist(reminder.toEntity())
    }

    @Transactional
    override fun countRemindersByConversationId(conversationId: PlainConversationId): Long =
        count("conversationId", conversationId)

}

