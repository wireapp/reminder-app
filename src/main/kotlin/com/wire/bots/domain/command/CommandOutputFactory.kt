package com.wire.bots.domain.command

object CommandOutputFactory {

    fun createLegacyHelpMessage(): String {
        return """
            Hi, I'm the Reminders bot.
            Please use my specific help command **`/remind.help`** to get more information about how to use me.
            """.trimIndent()
    }

    fun createHelpMessage(): String {
        return """
            Hi, I'm the Reminders bot.
            I can help you to create reminders for your conversations, or yourself.
        
            1. You can start by creating a reminder with the following command, some valid examples are:
            
            - **`/remind.new "do something" "in 5 minutes"`**
            - **`/remind.new "do something" "today at 9am"`**
            - **`/remind.new "do something" "18/09/2024 at 7pm"`**
            - **`/remind.new "do something" "next monday at 9am"`**
            
            You can also create a reminder that repeats, for example:
            
            - **`/remind.new "do something" "every day at 10am"`**
            
            2. You can list all the active reminders in the conversation with the following command:
            
            - **`/remind.list`**
            
            3. And you can delete a reminder with the following command:
            
            - **`/remind.delete <reminderId>`** (you can get the <reminderId> from the list command)
            """.trimIndent()
    }
}