package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.SimpleKotlinMultiplatformPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class InitFileTask : DefaultTask() {

    @get:Input
    abstract val fileForInit: Property<File>

    @get:Input
    abstract val resourceName: Property<String>

    @get:Input
    @get:Optional
    abstract val lineTransformer: Property<(String) -> String>

    init {
        group = INIT_GROUP_NAME
    }

    @TaskAction
    fun initFile() {
        val file = fileForInit.orNull
            ?: throw GradleException("Can not find file for initializing. Please set input for task")

        if (file.exists()) {
            return
        }

        file.absoluteFile.parentFile.mkdirs()

        val fileContent =
            SimpleKotlinMultiplatformPlugin::class.java.classLoader.getResourceAsStream(
                resourceName.orNull
                    ?: throw GradleException("Can not find resource for initializing. Please set input for task")
            ) ?: throw GradleException("Can not load file content")


        fileContent.use { fileInput ->
            file.outputStream().use { fileOutput ->
                if (lineTransformer.isPresent) {
                    val transformer = lineTransformer.get()
                    fileInput.bufferedReader().use { input ->
                        fileOutput.bufferedWriter().use { output ->
                            var line: String? = input.readLine()
                            while (line != null) {
                                output.appendLine(transformer(line))
                                line = input.readLine()
                            }

                        }
                    }
                } else {
                    fileInput.transferTo(fileOutput)
                }
            }
        }
    }

    companion object {
        const val INIT_GROUP_NAME = "init"
    }

}