package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class PropertyLayout(
    prefix: String?,
    val value: Map<String, *>
) {

    val prefix: String? = prefix?.let { "$it." }

    @OptIn(ExperimentalContracts::class)
    inline fun withPrefix(additionalPrefix: String, block: PropertyLayout.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        block(PropertyLayout(prefix?.let { "$it$additionalPrefix" } ?: additionalPrefix, value))
    }

    operator fun get(name: String): Any? =
        value[prefix?.let { it + name } ?: name]
}