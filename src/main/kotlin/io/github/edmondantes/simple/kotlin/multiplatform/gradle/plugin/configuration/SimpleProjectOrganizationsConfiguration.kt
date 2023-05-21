package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.organization.SimpleProjectOrganization
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.extension.SimpleProjectOrganizationExtension
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.extensionGetOrCreate
import org.gradle.api.GradleException
import org.gradle.api.Project

object SimpleProjectOrganizationsConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean = true
    private val ORGANIZATIONS = HashMap<String, SimpleProjectOrganization>()

    init {
        ORGANIZATIONS.register(SimpleProjectOrganization.GITHUB)
    }

    override fun configure(configurable: Project) {
        val extension = configurable.extensionGetOrCreate<SimpleProjectOrganizationExtension>("organizations")
        ORGANIZATIONS.putAll(extension.userOrganizations)
    }

    fun getByName(name: String): SimpleProjectOrganization = findByName(name)
        ?: throw GradleException("Can not find organization with name '$name'")

    fun findByName(name: String): SimpleProjectOrganization? =
        ORGANIZATIONS[name]

    private fun MutableMap<String, SimpleProjectOrganization>.register(organization: SimpleProjectOrganization): SimpleProjectOrganization? =
        put(organization.organizationName, organization)
}