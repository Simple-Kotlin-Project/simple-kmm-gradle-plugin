package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import org.gradle.api.Project

object SimpleKmmKoverConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean by pluginProperty { prefix = "kover"; defaultValue = true }

    override fun configure(configurable: Project) {
        configurable.pluginManager.apply("kover")
    }
}