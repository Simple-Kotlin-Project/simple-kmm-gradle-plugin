package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegate
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.PropertyDelegateBuilder
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.propertyPrefix
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

object SimpleKmmJavaConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean by javaProperty { defaultValue = true }
    private var targetCompatibility: String by javaProperty {
        defaultSupplier = SimpleKmmKotlinConfiguration::jvmTarget
    }
    var javaTargetCompatibilityVersion: JavaVersion
        get() = JavaVersion.toVersion(targetCompatibility)
        set(value) {
            targetCompatibility = value.name.substringAfter('_').replace('_', '.')
        }

    override fun configure(configurable: Project) {
        configurable.extensions.configure(JavaPluginExtension::class.java) {
            it.targetCompatibility = javaTargetCompatibilityVersion
        }
    }

    private inline fun <T> javaProperty(block: PropertyDelegateBuilder<T>.() -> Unit): PropertyDelegate<T> =
        pluginProperty {
            block()
            prefix = prefix.propertyPrefix("java")
        }
}