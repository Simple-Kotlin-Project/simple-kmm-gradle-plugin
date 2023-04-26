package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.license

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.InitFileTask
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.extensionGetOrCreate
import org.gradle.api.Project
import org.gradle.api.provider.Property

object SimpleLicenseConfiguration : Configuration<Project> {
    private lateinit var _license: Property<SimpleLicense>
    val license: SimpleLicense?
        get() = _license.orNull

    override fun configure(configurable: Project) {
        _license = configurable.extensionGetOrCreate<SimpleLicenseExtension>("licenses").license

        configurable.afterEvaluate {
            if (license != null) {
                configurable.tasks.register("initLicense", InitFileTask::class.java) {
                    it.description = "Initialize '$LICENSE_FILE' file"
                    it.fileForInit.set(configurable.file(LICENSE_FILE))
                    it.resourceName.set("init/license/${license!!.name}")
                }

                configurable.tasks.register("initLicenseHeaderFile", InitFileTask::class.java) {
                    it.description = "Initialize '$LICENCE_HEADER_FILE' file"
                    it.fileForInit.set(configurable.file(LICENCE_HEADER_FILE))
                    it.resourceName.set("init/license/file_header/${license!!.name}")
                }
            }
        }
    }

    const val LICENSE_FILE = "LICENSE.txt"
    const val LICENCE_HEADER_FILE = "LICENSE_FILE_HEADER"
}