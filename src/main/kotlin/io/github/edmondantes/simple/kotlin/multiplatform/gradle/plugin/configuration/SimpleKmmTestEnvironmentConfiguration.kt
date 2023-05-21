package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import groovy.json.StringEscapeUtils
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.extension.SimpleKmmTestEnvironmentExtension
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.InitFileTask
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.git.GitAddTask
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.git.GitNoSkipWorktreeTask
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.git.GitSkipWorktreeTask
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.extensionGetOrCreate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegateBuilder
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.propertyPrefix
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

object SimpleKmmTestEnvironmentConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean by testEnvProperty { defaultValue = true }
    var variablesFilePath: String by testEnvProperty { defaultValue = "./src/commonTest/kotlin/env/Env.kt" }
    var isTestLoggingEnabled: Boolean by testEnvProperty { defaultValue = false }
    var variablesDefaultIncluded: Boolean by testEnvProperty { defaultValue = true }

    private lateinit var extension: SimpleKmmTestEnvironmentExtension

    override fun configure(configurable: Project) {
        configurable.preConfigure()
        configurable.configureTasks()
    }

    private fun Project.preConfigure() {
        extension = extensionGetOrCreate<SimpleKmmTestEnvironmentExtension>("simpleTestEnv")
        if (variablesDefaultIncluded) {
            extension.putAll(DEFAULT_ENVIRONMENT_VARIABLES)
        }
    }

    private fun Project.configureTasks() {
        val envFile = file(variablesFilePath)
        val envFilePath = envFile.toPath()
        val gitStartTrack = tasks.create("gitStartTrackEnvFile", GitNoSkipWorktreeTask::class.java) {
            it.inputPath.set(envFilePath)
        }

        val gitAdd = tasks.create("gitAddEnvFile", GitAddTask::class.java) {
            it.inputPath.set(envFilePath)
        }

        val gitEndTrack = tasks.create("gitEndTrackEnvFile", GitSkipWorktreeTask::class.java) {
            it.inputPath.set(envFilePath)
        }

        val updateTestEnvFile = tasks.create("updateTestEnvFile") {
            it.group = TEST_ENVIRONMENT_GROUP_NAME
            it.description = "Update Env.kt file"
            it.doLast {
                if (!envFile.exists()) {
                    logger.warn("'Env.kt' file is not exists. Please start task 'initTestEnvFile'")
                    return@doLast
                }

                val envFileContent = envFile.readText()
                val startIndex = envFileContent.indexOf(ENV_FILE_OBJECT_DEFINITION) + ENV_FILE_OBJECT_DEFINITION.length
                val endIndex = envFileContent.lastIndexOf('}')

                val variablesText = StringBuilder()

                extension.forEach { (key, value) ->
                    when (value) {
                        is String ->
                            variablesText.append("\n    const val $key: String = \"${StringEscapeUtils.escapeJava(value)}\"")

                        is Boolean ->
                            variablesText.append("\n    const val $key: Boolean = $value")

                        else ->
                            throw GradleException("Can not create environment variable '$key'. Unknown type of variable. You can use only 'String' or 'Boolean'")
                    }
                }
                if (extension.isNotEmpty()) {
                    variablesText.append('\n')
                }

                envFileContent.replaceRange(startIndex, endIndex, variablesText.toString())
                envFile.writeText(envFileContent.replaceRange(startIndex, endIndex, variablesText.toString()))
            }
        }

        tasks.create("initTestEnvFile", InitFileTask::class.java) {
            it.description = "Initialize 'Env.kt' file"
            it.fileForInit.set(envFile)
            it.resourceName.set("init/Env.kt")

            it.finalizedBy(gitStartTrack, gitAdd, gitEndTrack, updateTestEnvFile)
        }

        tasks.create("enableTestLogging") {
            it.group = TEST_ENVIRONMENT_GROUP_NAME
            it.doLast {
                extension[LOGGING_ENVIRONMENT_VARIABLE] = true
            }
            it.finalizedBy(updateTestEnvFile)
        }

        tasks.create("disableTestLogging") {
            it.group = TEST_ENVIRONMENT_GROUP_NAME
            it.doLast {
                extension[LOGGING_ENVIRONMENT_VARIABLE] = false
            }
            it.finalizedBy(updateTestEnvFile)
        }


        tasks.withType(Test::class.java) {
            it.dependsOn(updateTestEnvFile)
            if (isTestLoggingEnabled) {
                it.dependsOn("disableTestLogging")
                it.finalizedBy("enableTestLogging")
            }
        }
    }

    private const val TEST_ENVIRONMENT_GROUP_NAME = "test environment"
    private const val ENV_FILE_OBJECT_DEFINITION = "object Env {"
    private const val LOGGING_ENVIRONMENT_VARIABLE = "isEnableLogging"
    private val DEFAULT_ENVIRONMENT_VARIABLES = mapOf<String, Any>(LOGGING_ENVIRONMENT_VARIABLE to false)

    private inline fun <T> testEnvProperty(block: PropertyDelegateBuilder<T>.() -> Unit): PropertyDelegate<T> =
        pluginProperty {
            block()
            prefix = prefix.propertyPrefix("test.environment")
        }
}