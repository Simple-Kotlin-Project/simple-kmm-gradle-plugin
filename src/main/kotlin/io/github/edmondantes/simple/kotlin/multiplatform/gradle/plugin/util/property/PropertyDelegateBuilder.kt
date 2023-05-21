package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property

class PropertyDelegateBuilder<T> {
    var prefix: String? = null
    var defaultSupplier: PropertyDefaultValueSupplier<T>? = null
    var defaultValue: T?
        get() = error("Property 'defaultValue' doesn't support getting")
        set(value) {
            defaultSupplier = { value }
        }
    var mapper: PropertyMapper<T>? = null

    fun build(): PropertyDelegate<T> =
        PropertyDelegate(prefix, defaultSupplier, mapper)
}