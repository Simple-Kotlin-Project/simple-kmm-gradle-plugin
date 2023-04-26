package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.test

import groovy.json.StringEscapeUtils
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.InitFileTask
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.extensionGetOrCreate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.toBoolean
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.withPluginPrefix
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.internal.io.NullOutputStream
import java.io.ByteArrayOutputStream

object SimpleKmmTestEnvironmentConfiguration : Configuration<Project> {

    private lateinit var envFilePath: String
    private lateinit var extension: SimpleKmTestEnvironmentExtension
    private var enableLoggingInTests: Boolean = false

    override fun configure(configurable: Project) {
        configurable.preConfigure()
        configurable.configureInitTasks()
        configurable.configureTasks()
    }

    private fun Project.preConfigure() {
        val disabledDefaultEnvironmentVariables: Boolean
        properties.withPluginPrefix {
            withPrefix("test.env") {
                withPrefix("variables") {
                    envFilePath = get("file.path")?.toString() ?: DEFAULT_ENV_FILE_PATH
                    disabledDefaultEnvironmentVariables = get("default.enabled").toBoolean(true)
                }
                enableLoggingInTests = get("logging.enabled").toBoolean()
            }

            extension = extensionGetOrCreate<SimpleKmTestEnvironmentExtension>("simpleTestEnv")
        }

        if (!disabledDefaultEnvironmentVariables) {
            extension.putAll(DEFAULT_ENVIRONMENT_VARIABLES)
        }
    }

    private fun Project.configureInitTasks() {
        tasks.create("initTestEnvFile", InitFileTask::class.java) {
            it.description = "Initialize 'Env.kt' file"
            val envFile = file(envFilePath)
            it.fileForInit.set(envFile)
            it.resourceName.set("init/Env.kt")

            it.doLast {
                val output = NullOutputStream.INSTANCE
                val errorOutput = ByteArrayOutputStream()

                exec {
                    it.commandLine((GIT_START_TRACKING_COMMAND + envFile.absolutePath).split(' '))
                    it.isIgnoreExitValue = true
                    it.standardOutput = output
                    it.errorOutput = output
                }

                val gitAddCommandResult = exec {
                    it.commandLine((GIT_ADD_FILE_COMMAND + envFile.absolutePath).split(' '))
                    it.isIgnoreExitValue = true
                    it.standardOutput = output
                    it.errorOutput = errorOutput
                }

                if (gitAddCommandResult.exitValue == 0) {
                    errorOutput.reset()

                    exec {
                        it.commandLine((GIT_DISABLE_TRACKING_COMMAND + envFile.absolutePath).split(' '))
                        it.isIgnoreExitValue
                        it.standardOutput = output
                        it.errorOutput = errorOutput
                    }
                }

                println(errorOutput)
            }

            it.finalizedBy("updateTestEnvFile")
        }
    }

    private fun Project.configureTasks() {
        val updateTestEnvFile = tasks.create("updateTestEnvFile") {
            it.group = TEST_ENVIRONMENT_GROUP_NAME
            it.description = "Update Env.kt file"
            it.doLast {
                val envFile = file(envFilePath)
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
                            throw GradleException("Can not create environment variable '$key'. Unknown type of variable. Can use only 'String' or 'Boolean'")
                    }
                }
                if (extension.isNotEmpty()) {
                    variablesText.append('\n')
                }

                envFileContent.replaceRange(startIndex, endIndex, variablesText.toString())
                envFile.writeText(envFileContent.replaceRange(startIndex, endIndex, variablesText.toString()))
            }
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
            if (enableLoggingInTests) {
                it.dependsOn("disableTestLogging")
                it.finalizedBy("enableTestLogging")
            }
        }
    }

    private const val TEST_ENVIRONMENT_GROUP_NAME = "test environment"
    private const val DEFAULT_ENV_FILE_PATH = "src/commonTest/kotlin/env/Env.kt"
    private const val ENV_FILE_OBJECT_DEFINITION = "object Env {"
    private const val GIT_ADD_FILE_COMMAND = "git add "
    private const val GIT_DISABLE_TRACKING_COMMAND = "git update-index --skip-worktree "
    private const val GIT_START_TRACKING_COMMAND = "git update-index --no-skip-worktree "
    private const val LOGGING_ENVIRONMENT_VARIABLE = "isEnableLogging"
    private val DEFAULT_ENVIRONMENT_VARIABLES = mapOf<String, Any>(LOGGING_ENVIRONMENT_VARIABLE to true)
}