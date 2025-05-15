package com.wire.bots.domain.event

import com.wire.bots.domain.PlainConversationId
import com.wire.bots.domain.reminder.Reminder

sealed interface Event {
    val conversationId: PlainConversationId
    val token: String
}

sealed class Command(
    override val conversationId: PlainConversationId,
    override val token: String
) : Event {
    /**
     * Help event for bot usage
     */
    data class Help(
        override val conversationId: PlainConversationId,
        override val token: String
    ) : Command(conversationId, token)

    /**
     * Legacy Help event (without suffix) suggesting to use the new help event.
     */
    data class LegacyHelp(
        override val conversationId: PlainConversationId,
        override val token: String
    ) : Command(conversationId, token)

    /**
     * New reminder event, for the target conversation.
     */
    data class NewReminder(
        override val conversationId: PlainConversationId,
        override val token: String,
        val reminder: Reminder
    ) : Command(conversationId, token)

    /**
     * List reminders event, for the target conversation.
     */
    data class ListReminders(
        override val conversationId: PlainConversationId,
        override val token: String
    ) : Command(conversationId, token)
    /**
     * Delete reminder event, for the target conversation.
     */
    data class DeleteReminder(
        override val conversationId: PlainConversationId,
        override val token: String,
        val reminderId: String
    ) : Command(conversationId, token)
}

sealed class Signal(
    override val conversationId: PlainConversationId,
    override val token: String
) : Event {
    /**
     * Bot added to the conversation, time to save Token to ConversationId.
     */
    data class BotAdded(
        override val conversationId: PlainConversationId,
        override val token: String
    ) : Signal(conversationId, token)

    /**
     * Bot removed from the conversation, time to all related data from the conversation.
     */
    data class BotRemoved(
        override val conversationId: PlainConversationId,
        override val token: String
    ) : Signal(conversationId, token)
}

sealed class BotError(
    open val conversationId: PlainConversationId,
    open val token: String,
    open val reason: String = "Core error"
) : Exception() {
    /**
     * An event that can be ignored-skipped by the bot
     * For example, user added to the conversation, mentions, etc.
     *
     * This event should be logged, but not processed.
     */
    data object Skip : BotError("", "")

    /**
     * Unknown event, or error while parsing the event by the bot.o
     */
    data class Unknown(
        override val conversationId: PlainConversationId,
        override val token: String,
        override val reason: String = "Unknown event"
    ) : BotError(conversationId, token, reason)

    /**
     * Error while processing the reminder.
     */
    data class ReminderError(
        override val conversationId: PlainConversationId,
        override val token: String,
        val errorType: ErrorType
    ) : BotError(conversationId, token, errorType.message)

    enum class ErrorType(
        val message: String
    ) {
        DATE_IN_PAST("❌ Reminder date is in the past. Please provide a date in the future."),
        INCREMENT_IN_TIMEUNIT(
            "❌ Increment in time units is not allowed, try again with days, weeks or greater."
        ),
        PARSE_ERROR(
            "❌ I'm sorry, I didn't catch that. I can get a little confused at times. " +
                "Please try again with a different format or see examples with **`/remind help`**."
        )
    }
}
