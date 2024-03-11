package com.wire.bots.domain.event

import com.wire.bots.domain.PlainConversationId

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
    data class Help(override val conversationId: PlainConversationId, override val token: String) :
        Command(conversationId, token)

    /**
     * Legacy Help event (without suffix) suggesting to use the new help event.
     */
    data class LegacyHelp(override val conversationId: PlainConversationId, override val token: String) :
        Command(conversationId, token)

}

sealed class Signal(override val conversationId: PlainConversationId, override val token: String) : Event {
    /**
     * Bot added to the conversation, time to save Token to ConversationId.
     */
    data class BotAdded(override val conversationId: PlainConversationId, override val token: String) :
        Signal(conversationId, token)

    /**
     * Bot removed from the conversation, time to all related data from the conversation.
     */
    data class BotRemoved(override val conversationId: PlainConversationId, override val token: String) :
        Signal(conversationId, token)
}

sealed class Error(override val conversationId: PlainConversationId, override val token: String) : Event {

    /**
     * An event that can be ignored-skipped by the bot
     * For example, user added to the conversation, mentions, etc.
     *
     * This event should be logged, but not processed.
     */
    data object Skip : Error("", "")

    /**
     * Unknown event, or error while parsing the event by the bot.o
     */
    data class Unknown(
        override val conversationId: PlainConversationId,
        override val token: String,
        val reason: String = "Unknown event"
    ) : Error(conversationId, token)
}
