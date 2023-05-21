package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.pom

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.url.UrlParser
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.url.UrlType
import org.gradle.api.publish.maven.MavenPomScm

class SimpleScmSpec : Configuration<MavenPomScm> {
    override var isConfigurationEnabled: Boolean = true
    var url: String? = null
    var connection: String? = null
    var developerConnection: String? = null

    override fun configure(configurable: MavenPomScm) {
        configurable.url.set(url?.let { UrlParser.parse(it, UrlType.HTTP) })
        configurable.connection.set(connection?.let { UrlParser.parse(it, UrlType.SCM) })
        configurable.developerConnection.set(developerConnection?.let { UrlParser.parse(it, UrlType.SCM) })
    }
}