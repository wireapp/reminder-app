package com.wire.bots.domain.command

import arrow.core.Either
import com.wire.bots.domain.DomainComponent
import com.wire.bots.domain.message.OutgoingMessageRepository

/**
 * Process the command and acts accordingly, also handles errors
 *
 * For example, if the command is "help", it will send a help message to the conversation, or
 * if the command is unknown, it will do nothing and send an error message to the conversation.
 */
@DomainComponent
class CommandProcessor(val outgoingMessageRepository: OutgoingMessageRepository) {

    fun process(command: Command): Either<Throwable, Unit> =
        when (command) {
            is Command.LegacyHelp -> processLegacyHelp(command)
            is Command.Help -> processHelp(command)
            is Command.UnknownCommand -> TODO("Errors or invalid commands should be handled here, send message to the conversation.")
            is Command.IrrelevantCommand -> TODO("This guy should be ignored, but we should log it.")
        }

    private fun processLegacyHelp(command: Command.LegacyHelp): Either<Throwable, Unit> {
        return outgoingMessageRepository.sendMessage(
            command.conversationId, command.token, CommandOutputFactory.createLegacyHelpMessage()
        )
    }

    private fun processHelp(command: Command.Help): Either<Throwable, Unit> {
        return outgoingMessageRepository.sendMessage(
            command.conversationId, command.token, CommandOutputFactory.createHelpMessage()
        )
    }

}