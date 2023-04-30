# Simple kotlin multiplatform gradle plugin

[![GitHub](http://img.shields.io/github/license/edmondantes/simple-kotlinx-serialization-utils?style=flat-square)](https://github.com/EdmonDantes/simple-kotlinx-serialization-utils)
[![Kotlin](https://img.shields.io/badge/kotlin-1.8.0-blue.svg?logo=kotlin)](http://kotlinlang.org)

Gradle plugin for configure kotlin multiplatform project for Simple Kotlin Project

### Properties

| Property name                                   | Description                                                                                                                          | Default value                      |
|-------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| `simple.kmm.kotlin.serialization.enabled`       | Enable kotlin serialization gradle plugin                                                                                            | `false`                            |
| `simple.kmm.library.enabled`                    | Mark project as kmm library. Enable explicit api and etc.                                                                            | `false`                            |
| `simple.kmm.compile.only.platform`              | Disable common kmm target and compile targets only for current platform                                                              | `true`                             |
| `simple.kmm.compile.browser.enabled`            | Enable browser compilation for javascript target                                                                                     | `false`                            |
| `simple.kmm.git.default.branch`                 | Set default git branch                                                                                                               | `master`                           |
| `simple.kmm.sign.key.id`                        | Key id for signing                                                                                                                   |                                    |
| `simple.kmm.sign.private.key`                   | Private key for signing                                                                                                              |                                    |
| `simple.kmm.sign.password`                      | Password for private key for signing                                                                                                 |                                    |
| `simple.kmm.publish.repository.id`              | Sonatype repositories id for publish                                                                                                 |                                    |
| `simple.kmm.publish.repository.url`             | Sonatype repositories base url                                                                                                       |                                    |
| `simple.kmm.publish.username`                   | Sonatype username                                                                                                                    |                                    |
| `simple.kmm.publish.password`                   | Sonatype password                                                                                                                    |                                    |
| `simple.kmm.test.env.variables.file.path`       | Path to file with environment variables for kotlin multiplatform project                                                             | `src/commonTest/kotlin/env/Env.kt` |
| `simple.kmm.test.env.variables.default.enabled` | Enable default environment variables                                                                                                 | `true`                             |
| `simple.kmm.test.env.logging.enabled`           | Enable logging when starts gradle task `test`. If disable plugin will change variable `isEnableLogging` before and after task `test` | `false`                            |
