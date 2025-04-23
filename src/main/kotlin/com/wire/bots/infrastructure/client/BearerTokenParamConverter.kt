package com.wire.bots.infrastructure.client

import jakarta.ws.rs.ext.ParamConverter
import jakarta.ws.rs.ext.ParamConverterProvider
import jakarta.ws.rs.ext.Provider
import java.lang.reflect.Type

typealias BearerToken = String

/**
 * A utility function to safely cast an object to a specific type.
 * If the cast is not possible, it returns null instead of throwing a ClassCastException.
 *
 * @param input The object to be cast.
 * @return The object casted to the specified type, or null if the cast is not possible.
 */
inline fun <reified T> safeCast(input: Any): T? {
    return input as? T
}

/**
 * Param converter for BearerToken.
 * This will add "Bearer " prefix to the token.
 *
 */
class BearerTokenParamConverter : ParamConverter<BearerToken> {
    override fun toString(value: BearerToken?): BearerToken = "Bearer $value"

    override fun fromString(value: BearerToken?): BearerToken = "Bearer $value"
}

@Provider
class BearerTokenParamConverterProvider : ParamConverterProvider {
    override fun <T> getConverter(
        rawType: Class<T>,
        genericType: Type?,
        annotations: Array<Annotation?>?,
    ): ParamConverter<T>? {
        if (rawType == String::class.java) return safeCast<ParamConverter<T>>(BearerTokenParamConverter())
        return null
    }
}
