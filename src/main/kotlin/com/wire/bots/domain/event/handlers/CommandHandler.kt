package com.wire.bots.domain.event.handlers

import arrow.core.Either
import arrow.core.flatMap
import com.wire.bots.domain.DomainComponent
import com.wire.bots.domain.event.Command
import com.wire.bots.domain.message.OutgoingMessageRepository
import com.wire.bots.domain.reminder.Reminder
import com.wire.bots.domain.reminder.ReminderNextSchedule
import com.wire.bots.domain.usecase.DeleteReminderUseCase
import com.wire.bots.domain.usecase.ListRemindersInConversation
import com.wire.bots.domain.usecase.SaveReminderSchedule

@DomainComponent
class CommandHandler(
    private val outgoingMessageRepository: OutgoingMessageRepository,
    private val saveReminderSchedule: SaveReminderSchedule,
    private val listRemindersInConversation: ListRemindersInConversation,
    private val deleteReminder: DeleteReminderUseCase,
) : EventHandler<Command> {
    override fun onEvent(event: Command): Either<Throwable, Unit> =
        when (event) {
            is Command.LegacyHelp ->
                outgoingMessageRepository.sendMessage(
                    event.conversationId,
                    event.token,
                    createLegacyHelpMessage()
                )

            is Command.Help ->
                outgoingMessageRepository.sendMessage(
                    event.conversationId,
                    event.token,
                    createHelpMessage()
                )

            is Command.NewReminder -> handleNewReminder(event)
            is Command.ListReminders -> getReminderListMessage(event)
            is Command.DeleteReminder -> deleteReminder(event)
        }

    private fun getReminderListMessage(command: Command.ListReminders): Either<Throwable, Unit> =
        listRemindersInConversation(command.conversationId).flatMap { reminders ->
            val message =
                if (reminders.isEmpty()) {
                    "There are no reminders yet in this conversation."
                } else {
                    "The reminders in this conversation:\n" +
                        reminders.joinToString("\n") {
                            "- What? ${it.task} (**${it.taskId}**)"
                        }
                }

            outgoingMessageRepository.sendMessage(command.conversationId, command.token, message)
        }

    private fun handleNewReminder(command: Command.NewReminder): Either<Throwable, Unit> =
        saveReminderSchedule(command.reminder).flatMap {
            outgoingMessageRepository.sendMessage(
                command.conversationId,
                command.token,
                getCreatedMessage(it)
            )
        }
    private fun deleteReminder(command: Command.DeleteReminder): Either<Throwable, Unit> =
        listRemindersInConversation(command.conversationId).flatMap { reminders ->
            val reminder = reminders.find { it.taskId == command.reminderId }
            if (reminder != null) {
                deleteReminder.invoke(reminder.taskId, reminder.conversationId).flatMap {
                    outgoingMessageRepository.sendMessage(
                        command.conversationId,
                        command.token,
                        "The reminder '${reminder.task}' was deleted."
                    )
                }
            } else {
                outgoingMessageRepository.sendMessage(
                    command.conversationId,
                    command.token,
                    "The reminder with id '${command.reminderId}' was not found."
                )
            }
        }

    private fun getCreatedMessage(reminderNextSchedule: ReminderNextSchedule): String =
        when (val reminder = reminderNextSchedule.reminder) {
            is Reminder.SingleReminder -> {
                "I will remind you to '${reminder.task}' " +
                    "at ${reminderNextSchedule.nextSchedules.first()}.\n" +
                    "If you want to delete it, you can use the command " +
                    "`/remind delete ${reminder.taskId}`"
            }

            is Reminder.RecurringReminder -> {
                "I will periodically remind you to '${reminder.task}'.\n" +
                    "If you want to delete it, you can use the command " +
                    "`/remind delete ${reminder.taskId}`\n\n" +
                    "The next ${reminderNextSchedule.nextSchedules.size} " +
                    "schedules for the reminder is:\n" +
                    reminderNextSchedule.nextSchedules.joinToString("\n") {
                        "- $it"
                    }
            }
        }

    companion object {
        fun createLegacyHelpMessage(): String =
            """
            Hi, I'm the Reminders bot.
            Please use my specific help event **`/remind help`** to get more information about how to use me.
            """.trimIndent()

        fun createHelpMessage(): String =
            """
            Hi, I'm the Reminders bot.
            I can help you to create reminders for your conversations, or yourself.
            
            1. You can start by creating a reminder with the following event, some valid examples are:
            
            - **`/remind to "do something" "in 5 minutes"`**
            - **`/remind to "do something" "today at 21:00"`**
            - **`/remind to "do something" "18/09/2024 at 09:45"`**
            - **`/remind to "do something" "next monday at 17:00"`**
            
            You can also create a reminder that repeats, for example:
            
            - **`/remind to "Start the daily stand up" "every day at 10:00"`**
            
            2. You can list all the active reminders in the conversation with the following event:
            
            - **`/remind list`**
            
            3. And you can delete a reminder with the following event:
            
            - **`/remind delete <reminderId>`** (you can get the <reminderId> from the list event)
            """.trimIndent()
    }
}
