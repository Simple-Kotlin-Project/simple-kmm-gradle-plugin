package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.configuration

import fr.brouillard.oss.gradle.plugins.JGitverPluginExtension
import fr.brouillard.oss.jgitver.Strategies
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.Configuration
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.pluginProperty
import org.gradle.api.Project

object SimpleKmmJGitVerConfiguration : Configuration<Project> {
    override var isConfigurationEnabled: Boolean by pluginProperty { prefix = "jgitver"; defaultValue = true }

    override fun configure(configurable: Project): Unit = configurable.run {
        pluginManager.apply("fr.brouillard.oss.gradle.jgitver")

        extensions.configure(JGitverPluginExtension::class.java) {
            it.strategy(Strategies.PATTERN)
            it.nonQualifierBranches(SimpleKmmGitConfiguration.gitDefaultBranch)
            it.tagVersionPattern("\${v}\${<meta.DIRTY_TEXT}")
            it.versionPattern(
                "\${v}\${<meta.COMMIT_DISTANCE}\${<meta.GIT_SHA1_8}" +
                        "\${<meta.QUALIFIED_BRANCH_NAME}\${<meta.DIRTY_TEXT}-SNAPSHOT"
            )
        }
    }
}