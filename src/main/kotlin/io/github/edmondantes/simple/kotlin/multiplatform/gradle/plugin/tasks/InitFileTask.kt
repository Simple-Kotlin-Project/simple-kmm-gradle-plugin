package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.SimpleKotlinMultiplatformPlugin
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.SimpleKotlinMultiplatformPlugin.Companion.INIT_GROUP_NAME
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class InitFileTask : DefaultTask() {

    @get:Input
    abstract val fileForInit: Property<File>

    @get:Input
    abstract val resourceName: Property<String>

    init {
        group = INIT_GROUP_NAME
    }

    @TaskAction
    fun initFile() {
        val file = fileForInit.orNull
            ?: throw GradleException("Can not find file for initializing. Please set input for task")
        if (file.exists()) {
            logger.warn("File already exists")
            return
        }

        file.absoluteFile.parentFile.mkdirs()

        val fileContent =
            SimpleKotlinMultiplatformPlugin::class.java.classLoader.getResourceAsStream(
                resourceName.orNull
                    ?: throw GradleException("Can not find resource for initializing. Please set input for task")
            ) ?: throw GradleException("Can not load file content")

        fileContent.use { input ->
            file.outputStream().use { output ->
                input.transferTo(output)
            }
        }
    }

}