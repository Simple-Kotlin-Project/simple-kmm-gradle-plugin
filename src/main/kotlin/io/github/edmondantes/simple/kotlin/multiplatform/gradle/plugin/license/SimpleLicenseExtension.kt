package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.license

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.SimpleExtension
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class SimpleLicenseExtension @Inject constructor(objectFactory: ObjectFactory) : SimpleExtension {

    internal val license: Property<SimpleLicense> = objectFactory.property(SimpleLicense::class.java)

    fun apache2() {
        license.set(SimpleLicense.APACHE2)
    }

}