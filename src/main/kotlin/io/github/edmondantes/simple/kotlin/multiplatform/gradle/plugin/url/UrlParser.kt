package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.url

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.organization.SimpleProjectOrganizationsConfiguration

object UrlParser {

    fun parse(url: String, type: String? = null): String =
        if (url.startsWith('#')) {
            val raw = url.substring(1).split("::")
            val organization = SimpleProjectOrganizationsConfiguration.getByName(raw[0])

            organization.urlFactory.get(raw.subList(1, raw.size), type) ?: organization.urlFactory.get(emptyList())
            ?: error("Can not get url for organization '${organization.organizationName}'")
        } else {
            url
        }
}