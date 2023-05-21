package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util

interface Configuration<T> {

    var isConfigurationEnabled: Boolean
    fun configure(configurable: T)

}