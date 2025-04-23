package com.wire.bots.infrastructure

import kotlinx.serialization.json.Json

object LenientJson {
    val parser =
        Json {
            prettyPrint = true
            isLenient = true
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
}
