package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import groovy.json.StringEscapeUtils
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.extension.SimpleKmmTestEnvironmentExtension
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.InitFileTask
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.extensionGetOrCreate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.DEFAULT_PROPERTY_PREFIX
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegateBuilder
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.propertyPrefix
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Path

object SimpleKmmTestEnvironmentConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean by testEnvProperty { defaultValue = true }
    var isVariablesFileShouldInitBeforeBuild: Boolean by testEnvProperty { defaultValue = true }
    var variablesFileDirectory: String by testEnvProperty {
        defaultValue = "./build/generated/testEnvironmentKmm/src/commonTest/kotlin"
    }
    var variablesClassPath: String by testEnvProperty { defaultValue = "env/Env.kt" }
    var variablesDefaultIncluded: Boolean by testEnvProperty { defaultValue = true }

    override fun configure(configurable: Project): Unit = configurable.run {
        val extension = extensionGetOrCreate<SimpleKmmTestEnvironmentExtension>("simpleTestEnv")
        if (variablesDefaultIncluded) {
            extension.putAll(DEFAULT_ENVIRONMENT_VARIABLES)
        }

        extensions.configure(KotlinMultiplatformExtension::class.java) {
            it.sourceSets.getByName("commonTest").kotlin.srcDir(variablesFileDirectory)
        }

        val envFile = file(Path.of(variablesFileDirectory).resolve(variablesClassPath).toString())

        val updateTestEnvFile = tasks.create("updateTestEnvFile") {
            it.group = TEST_ENVIRONMENT_GROUP_NAME
            it.description = "Update Env.kt file"
            it.doLast {
                if (!envFile.exists()) {
                    logger.warn("'Env.kt' file is not exists. Please start task 'initTestEnvFile'")
                    return@doLast
                }

                val envVariablesPrefix = "$DEFAULT_PROPERTY_PREFIX.$TEST_ENVIRONMENT_PROPERTY_PREFIX."
                configurable.properties.filter { it.key.startsWith(envVariablesPrefix) }.map {
                    it.value?.also { value ->
                        extension[it.key.substring(envVariablesPrefix.length).substringBefore('.')] = value
                    }
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

        val initTestEnvFileTask = tasks.create("initTestEnvFile", InitFileTask::class.java) {
            it.description = "Initialize 'Env.kt' file"
            it.fileForInit.set(envFile)
            it.resourceName.set("init/Env.kt")

            it.finalizedBy(updateTestEnvFile)
        }

        tasks.withType(Test::class.java) {
            it.dependsOn(updateTestEnvFile)
        }

        if (isVariablesFileShouldInitBeforeBuild) {
            tasks.withType(KotlinCompile::class.java)
                .forEach { it.dependsOn(initTestEnvFileTask) }
        }
    }

    private const val TEST_ENVIRONMENT_GROUP_NAME = "test environment"
    private const val ENV_FILE_OBJECT_DEFINITION = "object Env {"

    private const val LOGGING_ENVIRONMENT_VARIABLE = "isEnableLogging"
    private val DEFAULT_ENVIRONMENT_VARIABLES = mapOf<String, Any>(LOGGING_ENVIRONMENT_VARIABLE to false)

    private inline fun <T> testEnvProperty(block: PropertyDelegateBuilder<T>.() -> Unit): PropertyDelegate<T> =
        pluginProperty {
            block()
            prefix = prefix.propertyPrefix(TEST_ENVIRONMENT_PROPERTY_PREFIX)
        }

    const val TEST_ENVIRONMENT_PROPERTY_PREFIX = "test.environment"
}