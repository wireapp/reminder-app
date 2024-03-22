package com.wire.bots

import arrow.core.Either
import org.junit.jupiter.api.Assertions.fail
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline infix fun <L, R> Either<L, R>.shouldSucceed(crossinline successAssertion: (R) -> Unit) {
    contract { returns() implies (this@shouldSucceed is Either.Right<R>) }
    this.fold({ fail("Expected a Right value but got Left") }) { successAssertion(it) }
}

@OptIn(ExperimentalContracts::class)
fun <L, R> Either<L, R>.shouldSucceed() {
    contract { returns() implies (this@shouldSucceed is Either.Right<R>) }
    shouldSucceed { }
}

@OptIn(ExperimentalContracts::class)
fun <L, R> Either<L, R>.shouldFail() {
    contract { returns() implies (this@shouldFail is Either.Left<L>) }
    shouldFail { }
}

@OptIn(ExperimentalContracts::class)
inline infix fun <L, R> Either<L, R>.shouldFail(crossinline failAssertion: (L) -> Unit) {
    contract { returns() implies (this@shouldFail is Either.Left<L>) }
    this.fold({ failAssertion(it) }) { fail("Expected a Left value but got Right") }
}