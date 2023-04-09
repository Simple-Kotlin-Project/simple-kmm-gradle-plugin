package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class TestTask : DefaultTask() {

    @TaskAction
    fun test() {
        println("Hello world")
    }

}