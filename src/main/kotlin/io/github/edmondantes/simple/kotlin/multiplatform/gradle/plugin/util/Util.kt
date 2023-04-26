package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util

import org.gradle.api.Project
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun Any?.toBoolean(default: Boolean = false): Boolean =
    this?.toString()?.toBooleanStrictOrNull() ?: default

@OptIn(ExperimentalContracts::class)
inline fun <T> Map<String, *>.withPluginPrefix(block: PropertyLayout.() -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(PropertyLayout(DEFAULT_PROPERTY_PREFIX, this))
}

@OptIn(ExperimentalContracts::class)
inline fun <T> Project.pluginProperties(block: PropertyLayout.() -> T): T {
    contract {
        callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
    }
    return properties.withPluginPrefix(block)
}

fun Project.gitDefaultBranch(): String = pluginProperties {
    get("git.default.branch")?.toString() ?: "master"
}

const val DEFAULT_PROPERTY_PREFIX = "simple.kmm"