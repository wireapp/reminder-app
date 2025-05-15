package com.wire.bots.infrastructure.repository

import arrow.core.Either
import com.wire.bots.domain.PlainConversationId
import com.wire.bots.domain.reminder.Reminder
import com.wire.bots.domain.reminder.ReminderRepository
import com.wire.bots.infrastructure.toDomain
import com.wire.bots.infrastructure.toEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class DefaultReminderRepository :
    PanacheRepository<ReminderEntity>,
    ReminderRepository {
    @Transactional
    override fun persistReminder(reminder: Reminder): Either<Throwable, Unit> =
        Either.catch {
            persist(reminder.toEntity())
        }

    @Transactional
    override fun countRemindersByConversationId(conversationId: PlainConversationId): Long {
        val result = count("conversationId", conversationId)
        return result
    }

    @Transactional
    override fun getReminderOnConversationId(
        conversationId: PlainConversationId
    ): Either<Throwable, List<Reminder>> =
        Either.catch {
            list("conversationId", conversationId).map { it.toDomain() }
        }
    @Transactional
    override fun deleteReminder(reminderId: String, conversationId: PlainConversationId): Either<Throwable, Unit> =
        Either.catch {
            delete("taskId = ?1 and conversationId = ?2", reminderId, conversationId)
        }
}
