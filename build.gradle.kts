import fr.brouillard.oss.gradle.plugins.JGitverPluginExtension
import fr.brouillard.oss.jgitver.Strategies

plugins {
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm") version "1.8.0"
    id("fr.brouillard.oss.gradle.jgitver") version "0.9.1"
}

group = "io.github.edmondantes"

repositories {
    mavenCentral()
}

dependencies {
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
        mavenLocal()
    }
    publications {
        create<MavenPublication>("simplePlugin") {
            from(components["java"])
            artifact(tasks.kotlinSourcesJar)
        }
    }
}

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "io.github.edmondantes.simple.kmm"
            implementationClass = "io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.TestTask"
        }
    }
}

extensions.configure<JGitverPluginExtension> {
    strategy(Strategies.PATTERN)
    nonQualifierBranches("master")
    tagVersionPattern("\${v}\${<meta.DIRTY_TEXT}")
    versionPattern("\${v}\${<meta.COMMIT_DISTANCE}\${<meta.GIT_SHA1_8}" +
            "\${<meta.QUALIFIED_BRANCH_NAME}\${<meta.DIRTY_TEXT}-SNAPSHOT")
}