package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.extension.SimpleProjectDevelopersExtension
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.pom.SimpleProjectDeveloper
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.extensionGetOrCreate
import org.gradle.api.Project

object SimpleProjectDevelopersConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean = true
    var developers: List<SimpleProjectDeveloper> = emptyList()
    override fun configure(configurable: Project) {
        developers = configurable.extensionGetOrCreate<SimpleProjectDevelopersExtension>("developers").developers
    }
}