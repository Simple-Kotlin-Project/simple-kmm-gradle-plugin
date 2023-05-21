package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.extension

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.organization.SimpleProjectOrganization
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.url.UrlFactory
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.SimpleExtension

open class SimpleProjectOrganizationExtension : SimpleExtension {

    internal val userOrganizations = HashMap<String, SimpleProjectOrganization>()

    fun organization(name: String, urlFactory: UrlFactory) {
        userOrganizations[name] = SimpleProjectOrganization(name, urlFactory)
    }
}