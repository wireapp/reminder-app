package com.wire.bots.domain.event.handlers

import arrow.core.Either

interface EventHandler<T> {
    fun onEvent(event: T): Either<Throwable, Unit>
}
