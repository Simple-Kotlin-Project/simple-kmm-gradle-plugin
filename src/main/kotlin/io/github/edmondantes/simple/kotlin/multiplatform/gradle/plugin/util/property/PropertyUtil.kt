package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun String?.propertyPrefix(prefix: String?): String? {
    if (prefix == null) {
        return this
    }

    if (this == null) {
        return prefix
    }

    return if (prefix.endsWith('.') && startsWith('.')) {
        prefix + substring(1)
    } else if (!prefix.endsWith('.') && !startsWith('.')) {
        "$prefix.$this"
    } else {
        prefix + this
    }
}

@OptIn(ExperimentalContracts::class)
internal inline fun <T> property(block: PropertyDelegateBuilder<T>.() -> Unit): PropertyDelegate<T> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return PropertyDelegateBuilder<T>().also(block).build()
}

@OptIn(ExperimentalContracts::class)
internal inline fun <T> pluginProperty(block: PropertyDelegateBuilder<T>.() -> Unit): PropertyDelegate<T> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return property {
        block()
        prefix = prefix.propertyPrefix(DEFAULT_PROPERTY_PREFIX)
    }
}

@OptIn(ExperimentalContracts::class)
fun <T> gradleProperty(block: PropertyDelegateBuilder<T>.() -> Unit) : PropertyDelegate<T> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return PropertyDelegateBuilder<T>().also(block).build()
}

const val DEFAULT_PROPERTY_PREFIX = "simple.kmm"