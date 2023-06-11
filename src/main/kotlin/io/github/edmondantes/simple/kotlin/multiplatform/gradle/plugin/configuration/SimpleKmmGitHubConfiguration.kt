package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.InitFileTask
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegateBuilder
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.propertyPrefix
import org.gradle.api.Project

object SimpleKmmGitHubConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean by githubProperty {
        defaultValue = SimpleKmmGitConfiguration.hasGitRepository
    }

    var javaDistribution: String by githubProperty { defaultValue = "corretto" }
    var checkWorkflowName: String by githubProperty { defaultValue = "check" }
    var sonatypePublishWorkflowName: String by githubProperty { defaultValue = "sonatypePublish" }
    var mavenPublishWorkflowName: String by githubProperty { defaultValue = "mavenPublish" }
    var isSonatypePublishEnabled: Boolean by githubProperty { defaultValue = false }

    override fun configure(configurable: Project) {
        if (!SimpleKmmGitConfiguration.hasGitRepository || !SimpleKmmJavaConfiguration.isConfigurationEnabled) {
            return
        }

        configurable.tasks.create("initGitHubCheckWorkflow", InitFileTask::class.java) {
            it.description = "Initialize the github workflow file for build and check project"
            it.resourceName.set("init/github/check_workflow")
            it.fileForInit.set(configurable.file(".github/workflows/$checkWorkflowName.yml"))
            it.lineTransformer.set(::workflowLineTransformer)
        }

        if (!SimpleKmmPublishConfiguration.isConfigurationEnabled || !SimpleKmmJGitVerConfiguration.isConfigurationEnabled) {
            return
        }

        if (isSonatypePublishEnabled) {
            configurable.tasks.create("initGitHubSonatypePublishWorkflow", InitFileTask::class.java) {
                it.description = "Initialize the github workflow file for publishing to sonatype repository"
                it.resourceName.set("init/github/sonatype_publish_workflow")
                it.fileForInit.set(configurable.file(".github/workflows/$sonatypePublishWorkflowName.yml"))
                it.lineTransformer.set(::workflowLineTransformer)
            }
        } else {
            configurable.tasks.create("initGitHubMavenPublishWorkflow", InitFileTask::class.java) {
                it.description = "Initialize the github workflow file for publishing to maven repository"
                it.resourceName.set("init/github/maven_publish_workflow")
                it.fileForInit.set(configurable.file(".github/workflows/$mavenPublishWorkflowName.yml"))
                it.lineTransformer.set(::workflowLineTransformer)
            }
        }
    }

    private fun workflowLineTransformer(line: String): String {
        var lineForWrite = StringBuilder(line)

        var found = false
        for ((regex, supplier) in SUBSTRING_FOR_REPLACE) {
            val matches = regex.findAll(lineForWrite)
            for (match in matches) {
                found = true
                lineForWrite = lineForWrite.setRange(match.range.first, match.range.last + 1, supplier())
            }
        }

        return if (found) lineForWrite.toString() else line
    }

    private inline fun <T> githubProperty(block: PropertyDelegateBuilder<T>.() -> Unit): PropertyDelegate<T> =
        pluginProperty {
            block()
            prefix = prefix.propertyPrefix("github")
        }


    private val SUBSTRING_FOR_REPLACE: Map<Regex, () -> String> = mapOf(
        "JAVA_DISTRIBUTION" to { javaDistribution },
        "JAVA_VERSION" to { SimpleKmmJavaConfiguration.javaTargetCompatibilityVersion.majorVersion },
        "GIT_DEFAULT_BRANCH" to { SimpleKmmGitConfiguration.gitDefaultBranch },
        "SPOTLESS_RATCHET_GIT_BRANCH" to { SimpleKmmSpotlessConfiguration.ratchetGitBranch }
    ).mapKeys { Regex("\\\$${it.key}\\\$") }
}