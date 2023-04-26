import fr.brouillard.oss.gradle.plugins.JGitverPluginExtension
import fr.brouillard.oss.jgitver.Strategies

plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.8.0"
    id("fr.brouillard.oss.gradle.jgitver") version "0.9.1"
    id("com.gradle.plugin-publish") version "1.2.0"
}

group = "io.github.edmondantes"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    withVersion("kotlin", "org.jetbrains.kotlin:kotlin-gradle-plugin")
    withVersion("kotlin", "org.jetbrains.kotlin:kotlin-serialization")
    withVersion("dokka", "org.jetbrains.dokka:dokka-gradle-plugin")
    withVersion("spotless", "com.diffplug.spotless:spotless-plugin-gradle")
    withVersion("kover", "org.jetbrains.kotlinx:kover")
    withVersion("jgitver", "fr.brouillard.oss.gradle:gradle-jgitver-plugin")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("../local-plugin-repository")
        }
    }
}

gradlePlugin {
    plugins {
        create("simpleGradleKmmPlugin") {
            id = "io.github.edmondantes.simple.kmm.gradle.plugin"
            displayName = "Gradle plugin for configure kotlin multiplatform project for Simple Kotlin Project"
            description = "Gradle plugin for configure kotlin multiplatform project for Simple Kotlin Project"
            implementationClass =
                "io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.SimpleKotlinMultiplatformPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/Simple-Kotlin-Project/simple-kmm-gradle-plugin"
    vcsUrl = "https://github.com/Simple-Kotlin-Project/simple-kmm-gradle-plugin"

    pluginTags = mapOf(
        "simpleGradleKmmPlugin" to listOf("kotlin", "kotlinMultiplatform", "kmm")
    )
}

fun DependencyHandlerScope.withVersion(name: String, plugin: String) {
    api(plugin version name)
}

infix fun String.version(name: String): String {
    val version = properties["version.$name"] ?: return this

    return "$this:$version"
}

extensions.configure<JGitverPluginExtension> {
    strategy(Strategies.PATTERN)
    nonQualifierBranches("master")
    tagVersionPattern("\${v}\${<meta.DIRTY_TEXT}")
    versionPattern(
        "\${v}\${<meta.COMMIT_DISTANCE}\${<meta.GIT_SHA1_8}" +
                "\${<meta.QUALIFIED_BRANCH_NAME}\${<meta.DIRTY_TEXT}-SNAPSHOT"
    )
}