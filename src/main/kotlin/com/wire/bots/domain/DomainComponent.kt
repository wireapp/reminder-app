package com.wire.bots.domain

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Stereotype
import java.lang.annotation.Inherited

/**
 * Annotation [Stereotype] for domain components, it is a specialization of [ApplicationScoped].
 * This is just a convenience annotation to try to balance pure domain, hex architecture with pragmatic CDI.
 */
@Stereotype
@ApplicationScoped
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class DomainComponent