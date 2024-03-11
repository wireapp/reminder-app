package com.wire.bots.domain.event.handlers

import arrow.core.Either
import com.wire.bots.domain.DomainComponent
import com.wire.bots.domain.event.Command
import com.wire.bots.domain.message.OutgoingMessageRepository

@DomainComponent
class CommandHandler(private val outgoingMessageRepository: OutgoingMessageRepository) : EventHandler<Command> {
    override fun onEvent(event: Command): Either<Throwable, Unit> {
        return when (event) {
            is Command.LegacyHelp ->
                outgoingMessageRepository.sendMessage(event.conversationId, event.token, createLegacyHelpMessage())

            is Command.Help ->
                outgoingMessageRepository.sendMessage(event.conversationId, event.token, createHelpMessage())
        }
    }

    companion object {
        fun createLegacyHelpMessage(): String {
            return """
            Hi, I'm the Reminders bot.
            Please use my specific help event **`/remind.help`** to get more information about how to use me.
            """.trimIndent()
        }

        fun createHelpMessage(): String {
            return """
            Hi, I'm the Reminders bot.
            I can help you to create reminders for your conversations, or yourself.
        
            1. You can start by creating a reminder with the following event, some valid examples are:
            
            - **`/remind.new "do something" "in 5 minutes"`**
            - **`/remind.new "do something" "today at 9am"`**
            - **`/remind.new "do something" "18/09/2024 at 7pm"`**
            - **`/remind.new "do something" "next monday at 9am"`**
            
            You can also create a reminder that repeats, for example:
            
            - **`/remind.new "do something" "every day at 10am"`**
            
            2. You can list all the active reminders in the conversation with the following event:
            
            - **`/remind.list`**
            
            3. And you can delete a reminder with the following event:
            
            - **`/remind.delete <reminderId>`** (you can get the <reminderId> from the list event)
            """.trimIndent()
        }
    }
}