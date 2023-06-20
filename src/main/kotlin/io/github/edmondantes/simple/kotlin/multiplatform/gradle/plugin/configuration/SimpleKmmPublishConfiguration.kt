package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.extension.SimpleKmmPublishExtension
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.pom.SimplePom
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegateBuilder
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.propertyPrefix
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.shouldEndedWith
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension

object SimpleKmmPublishConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean by publishProperty { defaultValue = true }

    var publishRepositoryUrl: String by pluginProperty { defaultValue = DEFAULT_REPO_PATH }
    var publishRepositoryId: String? by pluginProperty {}
    var publishUsername: String? by pluginProperty {}
    var publishPassword: String? by pluginProperty {}

    var signKeyId: String? by pluginProperty {}
    var signPrivateKey: String? by pluginProperty {}
    var signPassword: String? by pluginProperty {}

    override fun configure(configurable: Project) {
        configurable.pluginManager.apply("maven-publish")
        configurable.pluginManager.apply("signing")

        configurable.configureSingingPlugin()
        configurable.configureMavenPublishPlugin()
    }

    private fun Project.configureSingingPlugin() {
        if (!signKeyId.isNullOrBlank() && !signPrivateKey.isNullOrBlank() && !signPassword.isNullOrBlank()) {
            extensions.configure(SigningExtension::class.java) {
                it.useInMemoryPgpKeys(signKeyId, signPrivateKey, signPassword)
                it.sign(extensions.getByType(PublishingExtension::class.java).publications)
            }

            val signingTasks = tasks.withType(Sign::class.java)
            tasks.withType(AbstractPublishToMaven::class.java).configureEach {
                it.dependsOn(signingTasks)
            }
        }
    }

    private fun Project.configureMavenPublishPlugin() {
        val pomContainer =
            project.container(SimplePom::class.java) as NamedDomainObjectContainer<SimplePom>

        val pomExtension = SimpleKmmPublishExtension(pomContainer)
        extensions.add(SimpleKmmPublishExtension::class.java, "simplePom", pomExtension)

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
                    it.url = uri(publishRepositoryUrl.ifEmpty { DEFAULT_REPO_PATH }.shouldEndedWith('/'))
                    if (!publishRepositoryId.isNullOrBlank()) {
                        it.url = it.url.resolve(publishRepositoryId!!)
                    }
                    if (!publishUsername.isNullOrEmpty() && !publishPassword.isNullOrEmpty()) {
                        it.credentials {
                            it.username = publishUsername
                            it.password = publishPassword
                        }
                    }
                }
            }
        }

        // FIXME: Disable for now, because kotlin multiplatform gradle plugin can not publish to one repository from different sources
//        project.afterEvaluate {
//            tasks.withType(AbstractPublishToMaven::class.java)
//                .matching { it.publication.name == KOTLIN_MULTIPLATFORM_PUBLICATION_NAME }
//                .configureEach { it.isEnabled = !SimpleKmmKotlinConfiguration.isCompileOnlyPlatform }
//        }
    }

    private inline fun <T> publishProperty(block: PropertyDelegateBuilder<T>.() -> Unit): PropertyDelegate<T> =
        pluginProperty {
            block()
            prefix = prefix.propertyPrefix("publish")
        }

    private const val KOTLIN_MULTIPLATFORM_PUBLICATION_NAME = "kotlinMultiplatform"
    private const val DEFAULT_REPO_PATH = "./build/repo/"
}