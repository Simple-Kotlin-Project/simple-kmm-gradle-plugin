package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.developer

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.extensionGetOrCreate
import org.gradle.api.Project

object SimpleProjectDevelopersConfiguration : Configuration<Project> {
    var developers: List<SimpleProjectDeveloper> = emptyList()
    override fun configure(configurable: Project) {
        developers = configurable.extensionGetOrCreate<SimpleProjectDevelopersExtension>("developers").developers
    }
}