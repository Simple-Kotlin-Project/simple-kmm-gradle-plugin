package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegateBuilder
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.propertyPrefix
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.target.KotlinNativeTarget
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.target.KotlinNativeTargetOs
import org.gradle.api.InvalidUserCodeException
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType
import org.jetbrains.kotlin.gradle.plugin.extraProperties

object SimpleKmmKotlinConfiguration : Configuration<Project> {

    private const val TASK_GROUP_PUBLISHING = "publishing"

    override var isConfigurationEnabled: Boolean by kotlinProperty { defaultValue = true }
    var isSerializationPluginEnabled: Boolean by kotlinProperty { defaultValue = false }
    var isExplicitApiEnabled: Boolean by kotlinProperty { defaultValue = false }
    var isCompileTargetJvmEnabled: Boolean by kotlinProperty { defaultValue = true }
    var isCompileTargetJsEnabled: Boolean by kotlinProperty { defaultValue = true }
    var isCompileTargetBrowserJsEnabled: Boolean by kotlinProperty { defaultValue = false }
    var isCompileTargetNativeEnabled: Boolean by kotlinProperty { defaultValue = false }
    var sdkJavaVersion: String by kotlinProperty { defaultValue = "11" }

    override fun configure(configurable: Project) = configurable.run {
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        if (isSerializationPluginEnabled) {
            project.pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
        }

        extensions.configure(KotlinMultiplatformExtension::class.java) {
            if (isExplicitApiEnabled) {
                it.explicitApi()
            }

            tasks.create("publishNotNativeToMavenRepository") {
                it.group = TASK_GROUP_PUBLISHING
                it.description = "Publishes all not native Maven publication to Maven repository 'maven'."
                val repositoryType = "repository"
                it.dependsOn(getPublicationTaskName("kotlinMultiplatform", repositoryType))
                if (isCompileTargetJvmEnabled) {
                    it.dependsOn(getPublicationTaskName("jvm", repositoryType))
                }

                if (isCompileTargetJsEnabled) {
                    it.dependsOn(getPublicationTaskName("js", repositoryType))
                }
            }

            tasks.create("publishNotNativeToMavenLocal") {
                it.group = TASK_GROUP_PUBLISHING
                it.description = "Publishes all not native Maven publication to the local Maven repository."
                val repositoryType = "local"
                it.dependsOn(getPublicationTaskName("kotlinMultiplatform", repositoryType))
                if (isCompileTargetJvmEnabled) {
                    it.dependsOn(getPublicationTaskName("jvm", repositoryType))
                }

                if (isCompileTargetJsEnabled) {
                    it.dependsOn(getPublicationTaskName("js", repositoryType))
                }
            }

            if (isCompileTargetJvmEnabled) {
                it.jvm {
                    withJava()
                    testRuns.getByName("test").executionTask.configure {
                        it.useJUnitPlatform()
                    }
                }

                JavaVersion.toVersion(sdkJavaVersion).majorVersion.toIntOrNull()?.also { jdkVersion ->
                    it.jvmToolchain(jdkVersion)
                }

            }

            if (isCompileTargetJsEnabled) {
                it.js(KotlinJsCompilerType.IR) {
                    if (isCompileTargetBrowserJsEnabled) {
                        browser()
                    }
                    nodejs()
                }
            }

            if (isCompileTargetNativeEnabled) {
                project.extraProperties.set("kotlin.native.ignoreDisabledTargets", "true")

                KotlinNativeTarget.values().map { it.targetName }.forEach { targetName ->
                    it.addTarget(targetName)
                }

                val osToTargets =
                    KotlinNativeTarget
                        .values()
                        .groupBy { it.osRunnable }
                        .mapValues { (_, list) -> list.map { it.targetName } }

                KotlinNativeTargetOs.values().filter { it != KotlinNativeTargetOs.UNKNOWN }.forEach { os ->

                    val osName = os.name.let { it[0].uppercase() + it.substring(1).lowercase() }

                    tasks.create("publishNativeFor${osName}ToMavenRepository") {
                        it.group = TASK_GROUP_PUBLISHING
                        it.description =
                            "Publishes Maven native publication for os '$osName' to Maven repository 'maven'."
                        osToTargets[os]?.map { getPublicationTaskName(it, "repository") }?.also { dependedTasks ->
                            it.dependsOn(dependedTasks)
                        }

                    }

                    tasks.create("publishNativeFor${osName}ToMavenLocal") {
                        it.group = TASK_GROUP_PUBLISHING
                        it.description =
                            "Publishes Maven native publication for os '$osName' to the local Maven repository."
                        osToTargets[os]?.map { getPublicationTaskName(it, "local") }?.also { dependedTasks ->
                            it.dependsOn(dependedTasks)
                        }
                    }

                }
            }

            configurable.tasks.create("printSupportNativeTargets") {
                it.description = "Prints all supports native targets"
                it.doLast {
                    println("Supports native targets:")
                    println("Tiers is correspond to https://kotlinlang.org/docs/native-target-support.html")
                    KotlinNativeTarget
                        .values()
                        .groupBy { it.tier }
                        .mapValues { it.value.map { it.targetName } }
                        .forEach { tier, targets ->
                            println("Tier $tier:")
                            targets.forEach {
                                println("\t* $it")
                            }
                        }
                }
            }
        }
    }

    private inline fun <T> kotlinProperty(block: PropertyDelegateBuilder<T>.() -> Unit): PropertyDelegate<T> =
        pluginProperty {
            block()
            prefix = prefix.propertyPrefix("kotlin")
        }

    private fun KotlinMultiplatformExtension.addTarget(targetName: String) {
        val targetPreset = presets.getByName(targetName)
        val existingTarget = targets.findByName(targetName)

        if (existingTarget == null) {
            val newTarget = targetPreset.createTarget(targetName)
            targets.add(newTarget)
        } else if (existingTarget.preset != targetPreset) {
            throw InvalidUserCodeException(
                "The target '$targetName' already exists, but it was not created with the '${targetPreset.name}' preset. " +
                        "To configure it, access it by name in `kotlin.targets`" +
                        (" or use the preset function '${existingTarget.preset?.name}'."
                            .takeIf { existingTarget.preset != null } ?: ".")
            )
        }
    }

    private fun getPublicationTaskName(publicationType: String, mavenRepositoryType: String): String {
        val publicationName = publicationType[0].uppercase() + publicationType.substring(1)
        val mavenRepositoryName = mavenRepositoryType[0].uppercase() + mavenRepositoryType.substring(1)
        return "publish${publicationName}PublicationToMaven${mavenRepositoryName}"
    }

}