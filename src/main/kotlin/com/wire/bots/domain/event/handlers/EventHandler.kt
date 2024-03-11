package com.wire.bots.domain.event.handlers

import arrow.core.Either
import com.wire.bots.domain.event.Event

interface EventHandler<T : Event> {
    fun onEvent(event: T): Either<Throwable, Unit>
}