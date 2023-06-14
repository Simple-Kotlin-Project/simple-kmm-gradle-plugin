package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegateBuilder
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.propertyPrefix
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

object SimpleKmmKotlinConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean by kotlinProperty { defaultValue = true }
    var isSerializationPluginEnabled: Boolean by kotlinProperty { defaultValue = false }
    var isLibraryConfigurationEnabled: Boolean by kotlinProperty { defaultValue = false }
    var isCompileOnlyPlatform: Boolean by kotlinProperty { defaultValue = true }
    var isCompileBrowserEnabled: Boolean by kotlinProperty { defaultValue = false }
    var jvmTarget: String by kotlinProperty { defaultValue = "11" }
    var isCompileByArm: Boolean by kotlinProperty { defaultValue = false }

    override fun configure(configurable: Project) = configurable.run {
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        if (isSerializationPluginEnabled) {
            project.pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
        }

        extensions.configure(KotlinMultiplatformExtension::class.java) {
            if (isLibraryConfigurationEnabled) {
                it.explicitApi()
            }
            if (!isCompileOnlyPlatform) {
                it.jvm {
                    compilations.all {
                        it.kotlinOptions.jvmTarget = jvmTarget
                    }
                    withJava()
                    testRuns.getByName("test").executionTask.configure {
                        it.useJUnitPlatform()
                    }
                }
                it.js(KotlinJsCompilerType.IR) {
                    if (isCompileBrowserEnabled) {
                        browser {
                            commonWebpackConfig {
                                cssSupport {
                                    it.enabled.set(true)
                                }
                            }
                        }
                    }
                    nodejs()
                }
            }

            val hostOs = System.getProperty("os.name")
            val isMingwX64 = hostOs.startsWith("Windows")
            if (isMingwX64) {
                it.mingwX64("native")
            } else {
                when (hostOs) {
                    "Mac OS X" -> {
                        if (isCompileByArm) {
                            it.macosArm64("native")
                        } else {
                            it.macosX64("native")
                        }
                    }

                    "Linux" -> {
                        if (isCompileByArm) {
                            it.linuxArm64("native")
                        } else {
                            it.linuxX64("native")
                        }
                    }

                    else -> throw GradleException("Host OS is not supported for this project")
                }
            }

            JavaVersion.toVersion(jvmTarget).majorVersion.toIntOrNull()?.also { jdkVersion ->
                it.jvmToolchain(jdkVersion)
            }

        }

        tasks.withType(Delete::class.java).configureEach {
            it.delete += listOf("$projectDir/kotlin-js-store")
        }
    }

    private inline fun <T> kotlinProperty(block: PropertyDelegateBuilder<T>.() -> Unit): PropertyDelegate<T> =
        pluginProperty {
            block()
            prefix = prefix.propertyPrefix("kotlin")
        }
}