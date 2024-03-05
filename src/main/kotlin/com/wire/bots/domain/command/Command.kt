package com.wire.bots.domain.command

import com.wire.bots.domain.PlainConversationId

sealed class Command(open val conversationId: PlainConversationId, open val token: String) {
    /**
     * Help command for bot usage
     */
    data class Help(override val conversationId: PlainConversationId, override val token: String) :
        Command(conversationId, token)

    /**
     * Legacy Help command (without suffix) suggesting to use the new help command.
     */
    data class LegacyHelp(override val conversationId: PlainConversationId, override val token: String) :
        Command(conversationId, token)

    /**
     * Unknown command
     */
    data class UnknownCommand(
        override val conversationId: PlainConversationId,
        override val token: String,
        val reason: String = "Unknown command"
    ) : Command(conversationId, token)

    /**
     * A command that can be ignored-skipped by the bot
     * For example, user added to the conversation, mentions, etc.
     */
    data object IrrelevantCommand : Command("", "")

}