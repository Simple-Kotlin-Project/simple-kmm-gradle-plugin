package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.git

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import java.io.OutputStream
import java.util.function.Consumer

abstract class GitTask : DefaultTask() {

    @get:Input
    @get:Optional
    abstract val output: Property<OutputStream>

    @get:Input
    @get:Optional
    abstract val errorOutput: Property<OutputStream>

    @get:Input
    @get:Optional
    abstract val handler: Property<Consumer<Int>>
}