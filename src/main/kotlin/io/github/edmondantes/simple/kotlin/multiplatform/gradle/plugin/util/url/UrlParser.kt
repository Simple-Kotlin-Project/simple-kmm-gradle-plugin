package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.url

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleProjectOrganizationsConfiguration

object UrlParser {

    fun parse(url: String, type: UrlType? = null): String =
        if (url.startsWith('#')) {
            val raw = url.substring(1).split("::")
            val organization = SimpleProjectOrganizationsConfiguration.getByName(raw[0])

            organization.urlFactory.get(raw.subList(1, raw.size), type)
                ?: organization.urlFactory.get(emptyList())
                ?: error("Can not get url for organization '${organization.organizationName}'")
        } else {
            url
        }
}