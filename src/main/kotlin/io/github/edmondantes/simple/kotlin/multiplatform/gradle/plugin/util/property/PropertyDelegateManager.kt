package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property

import org.gradle.api.Project
import java.util.function.Consumer

object PropertyDelegateManager : Consumer<Project> {
    private var project: Project? = null

    @Volatile
    private var lastReset: Long = System.currentTimeMillis()

    override fun accept(project: Project) {
        this.project = project
    }

    fun getProperty(propertyName: String): Any? =
        project?.properties?.get(propertyName)

    fun shouldUpdateProperty(lastInitialization: Long): Boolean =
        lastInitialization < lastReset
}