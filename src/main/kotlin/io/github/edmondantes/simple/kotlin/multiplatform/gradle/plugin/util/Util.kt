package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util

fun String.shouldEndedWith(char: Char, ignoreCase: Boolean = false): String =
    if (!endsWith(char, ignoreCase)) {
        this + char
    } else {
        this
    }