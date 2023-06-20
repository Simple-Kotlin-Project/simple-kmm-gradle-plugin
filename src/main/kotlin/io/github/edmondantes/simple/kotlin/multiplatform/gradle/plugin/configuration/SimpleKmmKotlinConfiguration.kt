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
    var isExplicitApiEnable: Boolean by kotlinProperty { defaultValue = false }
    var isCompileTargetJvmEnabled: Boolean by kotlinProperty { defaultValue = true }
    var isCompileTargetJsEnabled: Boolean by kotlinProperty { defaultValue = true }
    var isCompileTargetBrowserJsEnabled: Boolean by kotlinProperty { defaultValue = false }
    var isCompileTargetNativeEnabled: Boolean by kotlinProperty { defaultValue = false }
    var isCompileTargetNativeArmEnabled: Boolean by kotlinProperty { defaultValue = false }
    var sdkJavaVersion: String by kotlinProperty { defaultValue = "11" }

    override fun configure(configurable: Project) = configurable.run {
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        if (isSerializationPluginEnabled) {
            project.pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
        }

        extensions.configure(KotlinMultiplatformExtension::class.java) {
            if (isExplicitApiEnable) {
                it.explicitApi()
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

            if (isCompileTargetNativeEnabled) {
                val hostOs = System.getProperty("os.name")
                val isMingwX64 = hostOs.startsWith("Windows")
                if (isMingwX64) {
                    it.mingwX64("native")
                } else {
                    when (hostOs) {
                        "Mac OS X" -> {
                            if (isCompileTargetNativeArmEnabled) {
                                it.macosArm64("native")
                            } else {
                                it.macosX64("native")
                            }
                        }

                        "Linux" -> {
                            if (isCompileTargetNativeArmEnabled) {
                                it.linuxArm64("native")
                            } else {
                                it.linuxX64("native")
                            }
                        }

                        else -> throw GradleException("Host OS is not supported for this project")
                    }
                }
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