package com.wire.bots.infrastructure.jobs

import com.wire.bots.infrastructure.repository.DefaultReminderRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class ReminderTaskExecutor(@Inject val reminderRepository: DefaultReminderRepository) {

    // todo find the task by id
    // todo get token from tokens table by convoId of the task
    // todo send message to the conversation with the token and the task
    @Transactional
    fun doWork(taskId: String) {
        val reminder = reminderRepository.find("taskId", taskId).firstResult()
        println("Reminder: $reminder")
    }
}
