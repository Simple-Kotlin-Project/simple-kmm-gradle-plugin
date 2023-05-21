package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.pom

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleProjectDevelopersConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.license.SimpleLicense
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration.SimpleLicenseConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.url.UrlParser
import org.gradle.api.Named
import org.gradle.api.publish.maven.MavenPom

class SimplePom(private val _name: String) : Named, Configuration<MavenPom> {
    override var isConfigurationEnabled: Boolean = true
    var title: String? = null
    var description: String? = null
    var url: String? = null
    var license: SimpleLicense? = null
    private var scmSpec: SimpleScmSpec? = null

    fun scm(block: SimpleScmSpec.() -> Unit) {
        scmSpec = SimpleScmSpec().also(block)
    }

    override fun configure(configurable: MavenPom) {
        configurable.name.set(title)
        configurable.description.set(description)
        configurable.url.set(url?.let { UrlParser.parse(it) })
        val developers = SimpleProjectDevelopersConfiguration.developers
        if (developers.isNotEmpty()) {
            configurable.developers { spec ->
                developers.forEach { developer ->
                    spec.developer {
                        developer.configure(it)
                    }
                }
            }
        }

        scmSpec?.also { scm ->
            configurable.scm {
                scm.configure(it)
            }
        }

        val currentLicense = license ?: SimpleLicenseConfiguration.license
        if (currentLicense != null) {
            configurable.licenses {
                currentLicense.configure(it)
            }
        }
    }

    override fun getName(): String =
        _name
}