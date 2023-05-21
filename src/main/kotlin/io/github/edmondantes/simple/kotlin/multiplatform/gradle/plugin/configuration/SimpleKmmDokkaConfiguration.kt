package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

object SimpleKmmDokkaConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean by pluginProperty { prefix = "dokka"; defaultValue = true }

    override fun configure(configurable: Project): Unit = configurable.run {
        pluginManager.apply("org.jetbrains.dokka")

        val dokkaHtml = tasks.withType(org.jetbrains.dokka.gradle.DokkaTask::class.java).firstOrNull()
        tasks.register("javadocJar", Jar::class.java) {
            it.archiveClassifier.set("javadoc")
            it.dependsOn(dokkaHtml)
            it.from(dokkaHtml?.outputDirectory)
        }
    }
}