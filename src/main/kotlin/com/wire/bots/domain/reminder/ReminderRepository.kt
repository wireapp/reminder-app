package com.wire.bots.domain.reminder

import arrow.core.Either
import com.wire.bots.domain.PlainConversationId

interface ReminderRepository {
    fun persistReminder(reminder: Reminder): Either<Throwable, Unit>

    fun countRemindersByConversationId(conversationId: PlainConversationId): Long

    fun getReminderOnConversationId(
        conversationId: PlainConversationId
    ): Either<Throwable, List<Reminder>>
}
