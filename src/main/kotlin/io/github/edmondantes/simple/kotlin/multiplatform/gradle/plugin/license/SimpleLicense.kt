package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.license

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import org.gradle.api.publish.maven.MavenPomLicenseSpec

enum class SimpleLicense(private val licenseName: String, private val url: String) :
    Configuration<MavenPomLicenseSpec> {

    APACHE2("The Apache License, Version 2.0", "http://www.apache.org/licenses/LICENSE-2.0.txt");

    override var isConfigurationEnabled: Boolean = true

    override fun configure(configurable: MavenPomLicenseSpec) {
        configurable.license {
            it.name.set(licenseName)
            it.url.set(url)
        }
    }


}
