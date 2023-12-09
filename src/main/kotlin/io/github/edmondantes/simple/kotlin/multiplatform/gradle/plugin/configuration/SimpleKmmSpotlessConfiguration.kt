package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import com.diffplug.gradle.spotless.SpotlessExtension
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegateBuilder
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.propertyPrefix
import org.gradle.api.Project

object SimpleKmmSpotlessConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean by spotlessProperty { defaultValue = true }
    var ktlintVersion: String by spotlessProperty { defaultValue = "1.0.1" }
    var licenseFileHeaderPath: String by spotlessProperty { defaultValue = "./LICENSE_FILE_HEADER" }
    var ratchetGitBranch: String by spotlessProperty { defaultValue = SimpleKmmGitConfiguration.gitDefaultBranch }

    override fun configure(configurable: Project) = configurable.run {
        pluginManager.apply("com.diffplug.spotless")

        extensions.configure(SpotlessExtension::class.java) {
            if (SimpleKmmGitConfiguration.hasGitRepository) {
                it.ratchetFrom(ratchetGitBranch)
            }

            it.encoding("UTF-8")
            it.kotlin {
                it.target("src/*/kotlin/**/*.kt")
                it.targetExclude(file(SimpleKmmTestEnvironmentConfiguration.variablesClassPath))
                it.ktlint(ktlintVersion)
                if (file(licenseFileHeaderPath).exists()) {
                    it.licenseHeaderFile(licenseFileHeaderPath)
                }
            }
        }
    }

    private inline fun <T> spotlessProperty(block: PropertyDelegateBuilder<T>.() -> Unit): PropertyDelegate<T> =
        pluginProperty {
            block()
            prefix = prefix.propertyPrefix("spotless")
        }


}