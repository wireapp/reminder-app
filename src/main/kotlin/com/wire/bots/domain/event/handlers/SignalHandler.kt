package com.wire.bots.domain.event.handlers

import arrow.core.Either
import com.wire.bots.domain.DomainComponent
import com.wire.bots.domain.event.Signal
import com.wire.bots.domain.usecase.DeleteConversationToken
import com.wire.bots.domain.usecase.SaveConversationToken

@DomainComponent
class SignalHandler(
    private val saveConversationToken: SaveConversationToken,
    private val deleteConversationToken: DeleteConversationToken,
) : EventHandler<Signal> {
    override fun onEvent(event: Signal): Either<Throwable, Unit> =
        when (event) {
            is Signal.BotAdded -> saveConversationToken(event.conversationId, event.token)
            is Signal.BotRemoved -> deleteConversationToken(event.conversationId)
        }
}
