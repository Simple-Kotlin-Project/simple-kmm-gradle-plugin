package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.pom

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleProjectOrganizationsConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.organization.SimpleProjectOrganization
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.url.UrlParser
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.url.UrlType
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.publish.maven.MavenPomDeveloper

class SimpleProjectDeveloper(objectFactory: ObjectFactory) : Configuration<MavenPomDeveloper> {

    override var isConfigurationEnabled: Boolean = true

    lateinit var id: String
    lateinit var name: String
    lateinit var email: String
    lateinit var url: String
    lateinit var organization: SimpleProjectOrganization
    lateinit var timezone: String
    val properties: MapProperty<String, String> = objectFactory.mapProperty(String::class.java, String::class.java)
    val roles = HashSet<String>()

    fun organizationName(name: String) {
        organization = SimpleProjectOrganizationsConfiguration.getByName(name)
    }

    fun role(role: String) {
        roles.add(role)
    }

    override fun configure(configurable: MavenPomDeveloper) {
        if (::id.isInitialized) {
            configurable.id.set(id)
        }

        if (::name.isInitialized) {
            configurable.name.set(name)
        }

        if (::email.isInitialized) {
            configurable.email.set(email)
        }

        if (::url.isInitialized) {
            configurable.url.set(UrlParser.parse(url, UrlType.HTTP))
        }

        if (::organization.isInitialized) {
            configurable.organization.set(organization.organizationName)
        }

        if (::organization.isInitialized) {
            configurable.organizationUrl.set(organization.urlFactory.get(emptyList()))
        }

        if (roles.isNotEmpty()) {
            configurable.roles.value(roles)
        }

        if (::timezone.isInitialized) {
            configurable.timezone.set(timezone)
        }

        configurable.properties.set(properties)
    }
}