package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.tasks.git

import org.gradle.api.tasks.TaskAction
import org.gradle.internal.io.NullOutputStream

abstract class GitNoSkipWorktreeTask : GitPathTask() {

    @TaskAction
    fun action() {
        val exitCode = project.exec {
            it.commandLine(listOf("git", "update-index", "--no-skip-worktree", inputPath.get().toAbsolutePath().toString()))
            it.isIgnoreExitValue = true
            it.standardOutput = output.getOrElse(NullOutputStream.INSTANCE)
            it.errorOutput = errorOutput.getOrElse(System.err)
        }

        handler.orNull?.accept(exitCode.exitValue)
    }
}