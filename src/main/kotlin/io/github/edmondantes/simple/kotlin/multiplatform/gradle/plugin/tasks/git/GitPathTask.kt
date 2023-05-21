package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.git

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import java.nio.file.Path

abstract class GitPathTask : GitTask() {

    @get:Input
    abstract val inputPath: Property<Path>
}