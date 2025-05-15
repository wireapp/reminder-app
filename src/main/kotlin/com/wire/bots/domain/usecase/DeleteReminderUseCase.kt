package com.wire.bots.domain.usecase

import arrow.core.Either
import com.wire.bots.domain.DomainComponent
import com.wire.bots.domain.PlainConversationId
import com.wire.bots.domain.reminder.ReminderRepository

/**
 * Delete reminder for a conversation.
 */
@DomainComponent
class DeleteReminderUseCase(
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke(reminderId: String, conversationId: PlainConversationId): Either<Throwable, Unit> =
        Either.catch {
            reminderRepository.deleteReminder(reminderId, conversationId)
        }
}
