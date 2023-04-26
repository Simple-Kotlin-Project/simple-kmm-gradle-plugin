package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.publish

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.toBoolean
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.withPluginPrefix
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension

object SimpleKmmPublishConfiguration : Configuration<Project> {

    override fun configure(configurable: Project) {
        configurable.pluginManager.apply("maven-publish")
        configurable.pluginManager.apply("signing")

        configurable.configureSingingPlugin()
        configurable.configureMavenPublishPlugin()
    }

    private fun Project.configureSingingPlugin() {
        extensions.configure(SigningExtension::class.java) {
            val keyId: String?
            val privateKey: String?
            val password: String?
            properties.withPluginPrefix {
                withPrefix("sign") {
                    keyId = get("key.id")?.toString()
                    privateKey = get("private.key")?.toString()
                    password = get("password")?.toString()
                }
            }
            if (!keyId.isNullOrBlank() && !privateKey.isNullOrBlank() && !password.isNullOrBlank()) {
                it.useInMemoryPgpKeys(keyId, privateKey, password)
                it.sign(extensions.getByType(PublishingExtension::class.java).publications)
            }
        }


        val signingTasks = tasks.withType(Sign::class.java)
        tasks.withType(AbstractPublishToMaven::class.java).configureEach {
            it.dependsOn(signingTasks)
        }
    }

    private fun Project.configureMavenPublishPlugin() {
        val isMainHost: Boolean =
            properties.withPluginPrefix { !get("compile.only.platform").toBoolean(true) }

        val pomContainer =
            project.container(SimplePom::class.java) as NamedDomainObjectContainer<SimplePom>

        val pomExtension = SimpleKmmPublishExtension(this, pomContainer)

        extensions.add(
            SimpleKmmPublishExtension::class.java,
            "simplePom",
            pomExtension
        )

        extensions.configure(PublishingExtension::class.java) {
            val javadocJar = tasks.getByName("javadocJar")

            it.publications { publicationContainer ->
                publicationContainer
                    .withType(MavenPublication::class.java)
                    .forEach { publication ->
                        project.afterEvaluate {
                            publication.groupId = project.group.toString()
                            publication.version = project.version.toString()


                            val pomAction = try {
                                pomContainer.getByName(publication.name)
                            } catch (e: UnknownDomainObjectException) {
                                pomContainer.findByName(publication.name)
                            }

                            pomAction?.configure(publication.pom)
                        }

                        publication.artifact(javadocJar)
                    }
            }

            it.repositories {
                it.maven {
                    val publishRepositoryId: String?
                    val publishRepositoryUrl: String?
                    properties.withPluginPrefix {
                        withPrefix("publish") {
                            publishRepositoryId = get("repository.id")?.toString()
                            publishRepositoryUrl = get("repository.url")?.toString()
                        }
                    }

                    val resultUrl =
                        if (publishRepositoryUrl.isNullOrBlank() || publishRepositoryId.isNullOrBlank()) {
                            publishRepositoryUrl.orEmpty().ifEmpty { "./build/repo/" }
                        } else {
                            var resolvedUrl = publishRepositoryUrl.ifEmpty { "./build/repo/" }
                            if (!resolvedUrl.endsWith('/') && !publishRepositoryId.startsWith('/')) {
                                resolvedUrl += '/'
                            }

                            resolvedUrl + publishRepositoryId
                        }

                    it.url = uri(resultUrl)

                    val username: String?
                    val password: String?

                    properties.withPluginPrefix {
                        withPrefix("publish") {
                            username = get("username")?.toString()
                            password = get("password")?.toString()
                        }
                    }
                    if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                        it.credentials {
                            it.username = username
                            it.password = password
                        }
                    }
                }
            }
        }

        project.afterEvaluate {
            tasks.withType(AbstractPublishToMaven::class.java)
                .matching { it.publication.name == KOTLIN_MULTIPLATFORM_PUBLICATION_NAME }
                .configureEach { it.isEnabled = isMainHost }
        }
    }

    private const val KOTLIN_MULTIPLATFORM_PUBLICATION_NAME = "kotlinMultiplatform"

}