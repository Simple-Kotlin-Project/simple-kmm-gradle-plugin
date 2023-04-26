package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util

import org.gradle.api.Project

interface SimpleExtension

inline fun <reified T : SimpleExtension> Project.extensionGet(): T =
    extensions.getByType(T::class.java)

inline fun <reified T : SimpleExtension> Project.extensionGetOrCreate(name: String, vararg args: Any): T =
    extensions.findByType(T::class.java) ?: extensions.create(name, T::class.java, *args)