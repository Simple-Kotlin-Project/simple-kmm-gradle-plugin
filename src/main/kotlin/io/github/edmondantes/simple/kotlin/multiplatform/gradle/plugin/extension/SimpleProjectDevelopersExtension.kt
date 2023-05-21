package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.extension

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.pom.SimpleProjectDeveloper
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.SimpleExtension
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class SimpleProjectDevelopersExtension @Inject constructor(private val objectFactory: ObjectFactory) :
    SimpleExtension {

    internal val developers = ArrayList<SimpleProjectDeveloper>()

    fun developer(block: SimpleProjectDeveloper.() -> Unit) {
        developers.add(SimpleProjectDeveloper(objectFactory).also(block))
    }

}

