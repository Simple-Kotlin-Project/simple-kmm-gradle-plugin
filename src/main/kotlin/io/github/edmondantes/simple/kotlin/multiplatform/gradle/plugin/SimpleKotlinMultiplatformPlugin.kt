package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleKmmDokkaConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleKmmGitConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleKmmGitHubConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleKmmJGitVerConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleKmmKotlinConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleKmmKoverConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleKmmPublishConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleKmmSpotlessConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleKmmTestEnvironmentConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleLicenseConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleProjectDevelopersConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleProjectOrganizationsConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.InitFileTask
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegateManager
import org.gradle.api.Plugin
import org.gradle.api.Project

class SimpleKotlinMultiplatformPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.run {
            PropertyDelegateManager.accept(project)

            configureProjectVariables()

            DEFAULT_CONFIGURATION.forEach {
                it.configure(project)
            }

            configureInitTasks()
        }
    }

    private fun Project.configureProjectVariables() {
        repositories.apply {
            mavenCentral()
            maven {
                it.name = "Sonatype Releases"
                it.url = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
            }
            maven {
                it.name = "Sonatype Snapshots"
                it.url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            }
        }
    }

    private fun Project.configureInitTasks() {
        tasks.create("initProject") {
            it.group = InitFileTask.INIT_GROUP_NAME
            it.dependsOn(tasks.withType(InitFileTask::class.java))
        }
    }

    companion object {
        private val DEFAULT_CONFIGURATION: List<Configuration<Project>> = listOf(
            SimpleKmmGitConfiguration,

            SimpleKmmKotlinConfiguration,

            SimpleKmmSpotlessConfiguration,
            SimpleKmmDokkaConfiguration,
            SimpleKmmKoverConfiguration,
            SimpleKmmJGitVerConfiguration,

            SimpleLicenseConfiguration,
            SimpleProjectOrganizationsConfiguration,
            SimpleProjectDevelopersConfiguration,
            SimpleKmmPublishConfiguration,
            SimpleKmmGitHubConfiguration,
            SimpleKmmTestEnvironmentConfiguration
        )
    }
}