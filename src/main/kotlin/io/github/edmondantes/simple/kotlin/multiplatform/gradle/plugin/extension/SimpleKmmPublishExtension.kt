package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.extension

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.pom.SimplePom
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

open class SimpleKmmPublishExtension(
    private val parent: NamedDomainObjectContainer<SimplePom>
) : NamedDomainObjectContainer<SimplePom> by parent {

    override fun create(name: String): SimplePom =
        SimplePom(name).also(::add)

    override fun create(name: String, configureAction: Action<in SimplePom>): SimplePom =
        create(name).also(configureAction::execute)

    fun any(action: Action<SimplePom>) {
        addRule("any") { publicationName ->
            val pom = create(publicationName)
            action.execute(pom)
        }
    }

    fun any(block: SimplePom.() -> Unit) {
        addRule("any") { publicationName ->
            val pom = create(publicationName)
            block(pom)
        }
    }
}