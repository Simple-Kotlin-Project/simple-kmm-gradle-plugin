package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.InitFileTask
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import org.gradle.api.Project
import org.gradle.internal.io.NullOutputStream

object SimpleKmmGitConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean = true

    val gitDefaultBranch: String by pluginProperty { defaultValue = "master" }
    var hasGitRepository: Boolean = false

    override fun configure(configurable: Project) {
        val output = NullOutputStream.INSTANCE
        hasGitRepository = configurable.exec {
            it.commandLine("git status".split(' '))
            it.isIgnoreExitValue = true
            it.standardOutput = output
            it.errorOutput = output
        }.exitValue == 0


        if (!hasGitRepository) {
            return
        }

        configurable.tasks.register("initGitIgnore", InitFileTask::class.java) {
            it.description = "Initialize '.gitignore' file"
            it.fileForInit.set(configurable.file(".gitignore"))
            it.resourceName.set("init/gitignore")
        }
    }
}