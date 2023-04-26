package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.pattern

interface Pattern<T, R> {

    fun pattern(input: T) : R

}