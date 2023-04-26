package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.publish

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.developer.SimpleProjectDevelopersConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.license.SimpleLicense
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.license.SimpleLicenseConfiguration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.url.UrlParser
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.gitDefaultBranch
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomScm

open class SimpleKmmPublishExtension(
    private val project: Project,
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

class SimplePom(private val _name: String) : Named, Configuration<MavenPom> {

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

class SimplePomGithubSpec(private val project: Project) {
    var account: String? = null
    var projectName: String? = null

    fun url(): String? =
        account?.let {
            "$GITHUB_URL/$it/${projectName()}"
        }

    fun connection(): String? =
        account?.let { "scm:git:git://github.com/$it/${projectName()}.git" }

    fun developerConnection(): String? =
        account?.let { "scm:git:ssh://github.com:$it/${projectName()}.git" }

    fun scmUrl(): String? =
        account?.let { "$GITHUB_URL/$it/${projectName()}/tree/${project.gitDefaultBranch()}" }

    private fun projectName(): String =
        projectName ?: project.name
}

class SimpleScmSpec : Configuration<MavenPomScm> {
    var url: String? = null
    var connection: String? = null
    var developerConnection: String? = null

    override fun configure(configurable: MavenPomScm) {
        configurable.url.set(url?.let { UrlParser.parse(it, "http") })
        configurable.connection.set(connection?.let { UrlParser.parse(it, "scm") })
        configurable.developerConnection.set(developerConnection?.let { UrlParser.parse(it, "scm") })
    }
}


private const val GITHUB_NAME = "github"
private const val GITHUB_URL = "https://www.github.com"