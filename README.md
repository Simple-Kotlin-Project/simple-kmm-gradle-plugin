# Simple Kotlin Multiplatform Gradle Plugin

[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.edmondantes.simple.kmm.gradle.plugin?color=green&style=flat-square)](https://plugins.gradle.org/plugin/io.github.edmondantes.simple.kmm.gradle.plugin)
[![License](http://img.shields.io/github/license/Simple-Kotlin-Project/simple-kmm-gradle-plugin?style=flat-square)](https://github.com/Simple-Kotlin-Project/simple-kmm-gradle-plugin/blob/master/LICENSE.txt)
[![Kotlin](https://img.shields.io/badge/kotlin-1.8.0-blue.svg?logo=kotlin&style=flat-square)](http://kotlinlang.org)
[![GitHub Workflow Status (with branch)](https://img.shields.io/github/actions/workflow/status/Simple-Kotlin-Project/simple-kmm-gradle-plugin/check.yml?branch=master&style=flat-square)](https://github.com/Simple-Kotlin-Project/simple-kmm-gradle-plugin/actions/workflows/check.yml)

Gradle plugin for configure kotlin multiplatform project for Simple Kotlin Project

<!-- TOC -->

* [Simple Kotlin Multiplatform Gradle Plugin](#simple-kotlin-multiplatform-gradle-plugin)
    * [How to add plugin to your Gradle project](#how-to-add-plugin-to-your-gradle-project)
    * [Configuration](#configuration)
        * [URL](#url)
        * [Common Kotlin Multiplatform Configuration](#common-kotlin-multiplatform-configuration)
            * [Properties](#properties)
        * [Java Configuration](#java-configuration)
            * [Properties](#properties-1)
        * [Git Configuration](#git-configuration)
            * [Properties](#properties-2)
        * [GitHub Configuration](#github-configuration)
            * [Properties](#properties-3)
        * [Spotless Configuration](#spotless-configuration)
            * [Properties](#properties-4)
        * [Dokka Configuration](#dokka-configuration)
            * [Properties](#properties-5)
        * [Kover Configuration](#kover-configuration)
            * [Properties](#properties-6)
        * [JGitVer Configuration](#jgitver-configuration)
            * [Properties](#properties-7)
        * [License Configuration](#license-configuration)
            * [Properties](#properties-8)
            * [Script](#script)
        * [Organizations Configuration](#organizations-configuration)
            * [Script](#script-1)
        * [Developers Configuration](#developers-configuration)
            * [Script](#script-2)
        * [Publishing Configuration](#publishing-configuration)
            * [Properties](#properties-9)
            * [Script](#script-3)
        * [Test Environments Variables Configuration](#test-environments-variables-configuration)
            * [Default Test Environment Variables](#default-test-environment-variables)
            * [Properties](#properties-10)
            * [Script](#script-4)

<!-- TOC -->

## How to add plugin to your Gradle project

Using the plugins DSL:

<details>
<summary>Kotlin DSL</summary>

```kotlin
plugins {
    id("io.github.edmondantes.simple.kmm.gradle.plugin") version "${simple_plugin_version}"
}
```

</details>

<details>
<summary>Groovy</summary>

```groovy
plugins {
    id "io.github.edmondantes.simple.kmm.gradle.plugin" version "${simple_plugin_version}"
}
```

</details>


Or using legacy plugin application:

<details>
<summary>Kotlin DSL</summary>

```kotlin
buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("io.github.edmondantes:simple-kotlin-multiplatform-gradle-plugin:${simple_plugin_version}")
    }
}

apply(plugin = "io.github.edmondantes.simple.kmm.gradle.plugin")
```

</details>

<details>
<summary>Groovy</summary>

```groovy
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "io.github.edmondantes:simple-kotlin-multiplatform-gradle-plugin:${simple_plugin_version}"
    }
}

apply plugin: "io.github.edmondantes.simple.kmm.gradle.plugin"
```

</details>

## Configuration

Plugin has two type of configuration: extensions and properties. The first is defined in build.gradle script,
the second is gradle properties

### URL

In plugin's fields that receive url you can use special format for getting organization url:

`#orgizationName::arg1::arg2`

If `UrlFactory` for `organizationName` was defined, it will try to create url for arguments

Example:

`#github::user::project::branch` -> `https://www.github.com/user/project/tree/branch`

### Common Kotlin Multiplatform Configuration

#### Properties

| Property name                                     | Description                                                                                | Default value |
|---------------------------------------------------|--------------------------------------------------------------------------------------------|---------------|
| `simple.kmm.kotlin.configuration.enabled`         | Enables setting up Kotlin configuration by the plugin                                      | `true`        |
| `simple.kmm.kotlin.serialization.plugin.enabled`  | Enables applying Kotlin Serialization Plugin                                               | `false`       |
| `simple.kmm.kotlin.library.configuration.enabled` | Marks a project as Kotlin Multiplatform library. Enable explicit api and etc.              | `false`       |
| `simple.kmm.kotlin.compile.only.platform`         | Disables common Kotlin Multiplatform targets and compile targets only for current platform | `true`        |
| `simple.kmm.kotlin.compile.browser.enabled`       | Enables browser compilation for javascript target                                          | `false`       |
| `simple.kmm.kotlin.compile.by.arm`                | Enables native compiler for ARM processor                                                  | `false`       |
| `simple.kmm.kotlin.jvm.target`                    | Sets JVM target                                                                            | `11`          |

### Git Configuration

#### Properties

| Property name                   | Description                            | Default value |
|---------------------------------|----------------------------------------|---------------|
| `simple.kmm.git.default.branch` | Sets default branch for git repository | `master`      |

### GitHub Configuration

#### Properties

| Property name                                      | Description                                                                                                                                                                   | Default value                                |
|----------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------|
| `simple.kmm.github.configuration.enabled`          | Enables setting up GitHub configuration by the plugin                                                                                                                         | `true` if found git repository, else `false` |
| `simple.kmm.github.java.distribution`              | Sets java distribution for github's workflows. All supported distributions you can find [here](https://github.com/marketplace/actions/setup-java-jdk#supported-distributions) | `corretto`                                   |
| `simple.kmm.github.java.version`                   | Sets java distributions version for github's workflows.                                                                                                                       | `11`                                         |
| `simple.kmm.github.check.workflow.name`            | Sets a file's name for github's workflow for build and check project                                                                                                          | `check`                                      |
| `simple.kmm.github.sonatype.publish.enabled`       | Configure github's workflow for publish artifacts for sonatype publishing, else configure for maven publishing                                                                | `false`                                      |
| `simple.kmm.github.sonatype.publish.workflow.name` | Sets a file's name for github's workflow for sonatype publishing                                                                                                              | `sonatypePublish`                            |
| `simple.kmm.github.maven.publish.workflow.name`    | Sets a file's name for github's workflow for maven publishing                                                                                                                 | `mavenPublish`                               |

### Spotless Configuration

#### Properties

| Property name                                  | Description                                             | Default value                              |
|------------------------------------------------|---------------------------------------------------------|--------------------------------------------|
| `simple.kmm.spotless.configuration.enabled`    | Enables setting up Spotless configuration by the plugin | `true`                                     |
| `simple.kmm.spotless.ktlint.version`           | Sets Ktlint version                                     | `0.48.2`                                   |
| `simple.kmm.spotless.license.file.header.path` | Sets path to a license file header for Spotless         | `LICENSE_FILE_HEADER`                      |
| `simple.kmm.spotless.ratchet.git.branch`       | Sets git branch for ratchetFrom for spotless            | value from `simple.kmm.git.default.branch` |

### Dokka Configuration

#### Properties

| Property name                            | Description                                          | Default value |
|------------------------------------------|------------------------------------------------------|---------------|
| `simple.kmm.dokka.configuration.enabled` | Enables setting up Dokka configuration by the plugin | `true`        |

### Kover Configuration

#### Properties

| Property name                            | Description                                          | Default value |
|------------------------------------------|------------------------------------------------------|---------------|
| `simple.kmm.kover.configuration.enabled` | Enables setting up Kover configuration by the plugin | `true`        |

### JGitVer Configuration

#### Properties

| Property name                              | Description                                            | Default value |
|--------------------------------------------|--------------------------------------------------------|---------------|
| `simple.kmm.jgitver.configuration.enabled` | Enables setting up JGitVer configuration by the plugin | `true`        |

### License Configuration

#### Properties

| Property name                              | Description                                            | Default value |
|--------------------------------------------|--------------------------------------------------------|---------------|
| `simple.kmm.license.configuration.enabled` | Enables setting up License configuration by the plugin | `true`        |
| `simple.kmm.license.file.path`             | Sets path to license file                              | `./LICENSE`   |

#### Script

If License Configuration is enabled, you will configure type of license:

<details>
<summary>Groovy</summary>

```groovy
licenses {
    license = SimpleLicense.APACHE2
}
```

</details>

<details>
<summary>Kotlin DSL</summary>

```kotlin
licenses {
    license.set(SimpleLicense.APACHE2)
}
```

</details>

### Organizations Configuration

You can define organizations for use in publishing, etc. GitHub is already defined. You can use it by name `github`

#### Script

Before create organization you should create `UrlFactory` which generate your organization's url.
If you don't use created organization in `publish` module, you can create `EmptyUrlFactory`

<details>
<summary>Kotlin DSL</summary>

```kotlin
val urlFactory = PatternUrlFactory()

... configuration urlFactory ...

organizations {
    organization("orgName", urlFactory)
}
```

</details>

### Developers Configuration

You can define project's developers for use in publishing, etc.

#### Script

<details>
<summary>Kotlin DSL</summary>

```kotlin
developers {
    developer {
        id = "developerId"
        name = "developer"
        email = "developer@dev.com"
        url = "developer-site.com"
        organizationName("developerOrganization")
        timezone = "UTC"
        roles.add("Developer")
        role("Team Lead")
        properties.put("key", "value")
    }

    developer {
        id = "developerSecond"
        name = "second developer"
    }
}
```

</details>

### Publishing Configuration

You can configure publishing to maven repository and POM for publishing

#### Properties

| Property name                              | Description                                                                   | Default value  |
|--------------------------------------------|-------------------------------------------------------------------------------|----------------|
| `simple.kmm.publish.configuration.enabled` | Enables setting up Publishing configuration by the plugin                     | `true`         |
| `simple.kmm.publish.repository.url`        | Url to maven repository                                                       | `./build/repo` |
| `simple.kmm.publish.repository.id`         | Id of repository in nexus repository (will be added to end of repository url) |                |
| `simple.kmm.publish.username`              | Username for publishing in maven repository                                   |                |
| `simple.kmm.publish.password`              | Password for publishing in maven repository                                   |                |
| `simple.kmm.sign.key.id`                   | Key id for signing publishing artefacts                                       |                |
| `simple.kmm.sign.private.key`              | Private key for signing publishing artefacts                                  |                |
| `simple.kmm.sign.password`                 | Keystore password for signing publishing artefacts                            |                |

#### Script

You can configure POM for publishing by extension:

<details>
<summary>Kotlin DSL</summary>

```kotlin
simplePom {
    create("publishingName") {
        title = "Application"
        description = "Application description"
        url = "application-site.com"
        license = SimpleLicense.APACHE2
        scm {
            url = "scm-site-url"
            connection = "scm-connection-url"
            developerConnection = "scm-developer-connection-url"
        }
    }

    any {
        title = "Application name"
    }
}
```

</details>

### Test Environments Variables Configuration

This plugin add possibility to set environments variables for kotlin multiplatform projects.

Plugin will generate special `kotlin object` which will contains variables which you can use in
kotlin multiplatform tests and configure by gradle extension

Plugin has default variables properties.

#### Default Test Environment Variables

| Property name     | Type      | Description                                                                                                       |
|-------------------|-----------|-------------------------------------------------------------------------------------------------------------------|
| `isEnableLogging` | `Boolean` | Enables logging in tests. This variable will change before and after tests if test logging disabled in properties |

#### Properties

| Property name                                                         | Description                                                   | Default value                                                |
|-----------------------------------------------------------------------|---------------------------------------------------------------|--------------------------------------------------------------|
| `simple.kmm.test.environment.configuration.enabled`                   | Enables setting up Test Environments Variables by the plugin  | `true`                                                       |
| `simple.kmm.test.environment.variables.file.directory`                | Path to directory which environment variables file's packages | `./build/generated/testEnvironmentKmm/src/commonTest/kotlin` |
| `simple.kmm.test.environment.variables.class.path`                    | Path to file with environments variables                      | `env/Env.kt`                                                 |
| `simple.kmm.test.environment.variables.file.should.init.before.build` | If true, start `initTestEnvFile` task before `build` task     | `true`                                                       |
| `simple.kmm.test.environment.variables.default.included`              | If true default variables will be added to file               | `true`                                                       |
| `simple.kmm.test.environment.set.propertyName`                        | Sets value to environment variable with name `propertyName`   |                                                              |

#### Script

You can configure variables by extension, but variables can be only `Boolean` and `String` type.

<details>
<summary>Kotlin DSL</summary>

```kotlin
simpleTestEnv {
    put("variableName", "value")
    put("isVariableName", true)
}
```

</details>