package com.wire.bots.infrastructure.repository

import com.wire.bots.domain.Reminder
import com.wire.bots.domain.ReminderRepository
import com.wire.bots.infrastructure.toEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class DefaultReminderRepository : PanacheRepository<ReminderEntity>, ReminderRepository {

    @Transactional
    override fun persistReminder(reminder: Reminder) {
        persist(reminder.toEntity())
    }

}

