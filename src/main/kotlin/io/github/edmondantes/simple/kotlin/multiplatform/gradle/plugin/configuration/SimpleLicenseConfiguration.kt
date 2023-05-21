package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.extension.SimpleLicenseExtension
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.license.SimpleLicense
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.InitFileTask
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.extensionGetOrCreate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegateBuilder
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.propertyPrefix
import org.gradle.api.Project
import org.gradle.api.provider.Property
import kotlin.io.path.absolute

object SimpleLicenseConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean by licenseProperty { defaultValue = true }
    var filePath: String by licenseProperty { defaultValue = "./LICENSE" }

    private lateinit var _license: Property<SimpleLicense>
    val license: SimpleLicense?
        get() = _license.orNull

    override fun configure(configurable: Project) {
        _license = configurable.extensionGetOrCreate<SimpleLicenseExtension>("licenses").license

        configurable.afterEvaluate {

            if (license == null) {
                return@afterEvaluate
            }

            it.run {
                val licenseFile = file(filePath)
                tasks.register("initLicense", InitFileTask::class.java) {
                    it.description = "Initialize '${licenseFile.toPath().absolute()}' file"
                    it.fileForInit.set(licenseFile)
                    it.resourceName.set("init/license/${license!!.name}")
                }


                val licenseHeaderFile =
                    file(SimpleKmmSpotlessConfiguration.licenseFileHeaderPath)

                tasks.register("initLicenseHeaderFile", InitFileTask::class.java) {
                    it.description =
                        "Initialize '${licenseHeaderFile.toPath().absolute()}' file"
                    it.fileForInit.set(licenseHeaderFile)
                    it.resourceName.set("init/license/file_header/${license!!.name}")
                }
            }
        }
    }

    private inline fun <T> licenseProperty(block: PropertyDelegateBuilder<T>.() -> Unit): PropertyDelegate<T> =
        pluginProperty {
            block()
            prefix = prefix.propertyPrefix("license")
        }
}