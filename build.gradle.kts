import fr.brouillard.oss.gradle.plugins.JGitverPluginExtension
import fr.brouillard.oss.jgitver.Strategies

plugins {
    `java-gradle-plugin`
    kotlin("jvm") version(libs.versions.kotlin)
    alias(libs.plugins.jgitver)
    alias(libs.plugins.publish)
}

group = "io.github.edmondantes"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    api(libs.kotlin.gradle)
    api(libs.kotlin.serialization)
    api(libs.dokka)
    api(libs.spotless)
    api(libs.kover)
    api(libs.jgitver)

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

extensions.configure<JGitverPluginExtension> {
    strategy(Strategies.PATTERN)
    nonQualifierBranches("master")
    tagVersionPattern("\${v}\${<meta.DIRTY_TEXT}")
    versionPattern(
        "\${v}\${<meta.COMMIT_DISTANCE}\${<meta.GIT_SHA1_8}" +
                "\${<meta.QUALIFIED_BRANCH_NAME}\${<meta.DIRTY_TEXT}-SNAPSHOT"
    )
}