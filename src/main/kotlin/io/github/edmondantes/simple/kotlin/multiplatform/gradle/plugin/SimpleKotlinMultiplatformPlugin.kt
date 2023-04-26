package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin

import com.diffplug.gradle.spotless.SpotlessExtension
import fr.brouillard.oss.gradle.plugins.JGitverPluginExtension
import fr.brouillard.oss.jgitver.Strategies
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.developer.SimpleProjectDevelopersConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.license.SimpleLicenseConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.license.SimpleLicenseConfiguration.LICENCE_HEADER_FILE
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.organization.SimpleProjectOrganizationsConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.publish.SimpleKmmPublishConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.InitFileTask
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.test.SimpleKmmTestEnvironmentConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.gitDefaultBranch
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.toBoolean
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.withPluginPrefix
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Delete
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

class SimpleKotlinMultiplatformPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.run {
            configureProjectVariables()

            configureKotlinPlugin()
            configureJavaPlugin()

            configureSpotlessPlugin()
            configureDokkaPlugin()
            configureKoverPlugin()

            configureJGitVerPlugin()

            DEFAULT_CONFIGURATION.forEach {
                it.configure(project)
            }

            configureInitTasks()
        }
    }

    private fun Project.configureProjectVariables() {
        group = PROJECT_GROUP

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

    private fun Project.configureJavaPlugin() {
        extensions.configure(JavaPluginExtension::class.java) {
            it.targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    private fun Project.configureKotlinPlugin() {
        val isEnableSerialization = properties.withPluginPrefix {
            get("kotlin.serialization.enabled").toBoolean()
        }

        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        if (isEnableSerialization) {
            project.pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
        }

        extensions.configure(KotlinMultiplatformExtension::class.java) {
            val isKmmLibrary: Boolean
            val isMainHost: Boolean
            properties.withPluginPrefix {
                isKmmLibrary = get("library.enabled").toBoolean()
                isMainHost = !get("compile.only.platform").toBoolean(true)
            }


            if (isKmmLibrary) {
                it.explicitApi()
            }
            if (isMainHost) {
                it.jvm {
                    compilations.all {
                        it.kotlinOptions.jvmTarget = "1.8"
                    }
                    withJava()
                    testRuns.getByName("test").executionTask.configure {
                        it.useJUnitPlatform()
                    }
                }
                it.js(KotlinJsCompilerType.IR) {
                    val hasBrowser: Boolean = properties.withPluginPrefix {
                        get("compile.browser.enabled").toBoolean()
                    }
                    if (hasBrowser) {
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
                it.mingwX64("mingwX64")
            } else {
                when (hostOs) {
                    "Mac OS X" -> {
                        it.macosX64("macosX64")
                        it.macosArm64("macosArm64")
                        it.iosX64("iosX64")
                        it.iosArm64("iosArm64")
                        it.watchosArm32("watchosArm32")
                        it.watchosArm64("watchosArm64")
                        it.watchosX64("watchosX64")
                    }

                    "Linux" -> {
                        it.linuxX64("linuxX64")
                        it.linuxArm64("linuxArm64")
                    }

                    else -> throw GradleException("Host OS is not supported for this project")
                }
            }
        }


        tasks.withType(Delete::class.java).configureEach {
            it.delete += listOf("$projectDir/kotlin-js-store")
        }
    }

    private fun Project.configureInitTasks() {
        tasks.register("initGitIgnore", InitFileTask::class.java) {
            it.description = "Initialize '.gitignore' file"
            it.fileForInit.set(file(".gitignore"))
            it.resourceName.set("init/gitignore")
        }

        tasks.create("initProject") {
            it.dependsOn(tasks.withType(InitFileTask::class.java))
        }
    }

    private fun Project.configureSpotlessPlugin() {
        pluginManager.apply("com.diffplug.spotless")

        extensions.configure(SpotlessExtension::class.java) {
            if (file("./.git").exists()) {
                it.ratchetFrom(gitDefaultBranch())
            }

            it.encoding("UTF-8")
            it.kotlin {
                it.target("src/*/kotlin/**/*.kt")
                it.targetExclude("src/**/Env.kt")
                it.ktlint(KTLINT_VERSION)
                it.licenseHeaderFile(LICENCE_HEADER_FILE)
            }
        }
    }

    private fun Project.configureDokkaPlugin() {
        pluginManager.apply("org.jetbrains.dokka")

        val dokkaHtml = tasks.withType(org.jetbrains.dokka.gradle.DokkaTask::class.java).firstOrNull()
        tasks.register("javadocJar", Jar::class.java) {
            it.archiveClassifier.set("javadoc")
            it.dependsOn(dokkaHtml)
            it.from(dokkaHtml?.outputDirectory)
        }
    }

    private fun Project.configureKoverPlugin() {
        pluginManager.apply("kover")
    }

    private fun Project.configureJGitVerPlugin() {
        pluginManager.apply("fr.brouillard.oss.gradle.jgitver")

        extensions.configure(JGitverPluginExtension::class.java) {
            it.strategy(Strategies.PATTERN)
            it.nonQualifierBranches(gitDefaultBranch())
            it.tagVersionPattern("\${v}\${<meta.DIRTY_TEXT}")
            it.versionPattern(
                "\${v}\${<meta.COMMIT_DISTANCE}\${<meta.GIT_SHA1_8}" +
                        "\${<meta.QUALIFIED_BRANCH_NAME}\${<meta.DIRTY_TEXT}-SNAPSHOT"
            )
        }
    }

    companion object {
        private const val PROJECT_GROUP = "io.github.edmondantes"
        private const val KTLINT_VERSION = "0.48.2"

        private val DEFAULT_CONFIGURATION: List<Configuration<Project>> = listOf(
            SimpleLicenseConfiguration,
            SimpleProjectOrganizationsConfiguration,
            SimpleProjectDevelopersConfiguration,
            SimpleKmmPublishConfiguration,
            SimpleKmmTestEnvironmentConfiguration
        )

        const val INIT_GROUP_NAME = "init"
    }
}