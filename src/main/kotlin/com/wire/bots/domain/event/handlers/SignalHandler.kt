package com.wire.bots.domain.event.handlers

import arrow.core.Either
import com.wire.bots.domain.DomainComponent
import com.wire.bots.domain.event.Signal
import com.wire.bots.domain.token.TokenRepository

@DomainComponent
class SignalHandler(private val tokenRepository: TokenRepository) : EventHandler<Signal> {

    override fun onEvent(event: Signal): Either<Throwable, Unit> {
        return when (event) {
            is Signal.BotAdded -> tokenRepository.insertToken(event.conversationId, event.token)
            is Signal.BotRemoved -> tokenRepository.deleteToken(event.conversationId)
        }
    }

}