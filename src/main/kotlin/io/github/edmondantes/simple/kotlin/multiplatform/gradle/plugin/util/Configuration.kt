package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util

interface Configuration<T> {

    fun configure(configurable: T)

}