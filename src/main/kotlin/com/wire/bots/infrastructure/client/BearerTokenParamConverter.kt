package com.wire.bots.infrastructure.client

import jakarta.ws.rs.ext.ParamConverter
import jakarta.ws.rs.ext.ParamConverterProvider
import jakarta.ws.rs.ext.Provider
import java.lang.reflect.Type

typealias BearerToken = String

/**
 * Param converter for BearerToken.
 * This will add "Bearer " prefix to the token.
 *
 */
class BearerTokenParamConverter : ParamConverter<BearerToken> {

    override fun toString(value: BearerToken?): BearerToken {
        return "Bearer $value"
    }

    override fun fromString(value: BearerToken?): BearerToken {
        return "Bearer $value"
    }
}

@Provider
class BearerTokenParamConverterProvider : ParamConverterProvider {
    override fun <T> getConverter(
        rawType: Class<T>,
        genericType: Type?,
        annotations: Array<Annotation?>?
    ): ParamConverter<T>? {
        if (rawType == String::class.java) return BearerTokenParamConverter() as (ParamConverter<T>)
        return null
    }
}
