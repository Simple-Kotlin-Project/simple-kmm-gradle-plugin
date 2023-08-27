package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.target

enum class KotlinNativeTargetOs {
    WINDOWS,
    LINUX,
    MACOS,
    UNKNOWN
}

fun getRunningOs(): KotlinNativeTargetOs {
    val osName = System.getProperty("os.name")
    return when {
        osName == "Linux" -> KotlinNativeTargetOs.LINUX
        osName.startsWith("Windows") -> KotlinNativeTargetOs.WINDOWS
        osName.startsWith("Mac") -> KotlinNativeTargetOs.MACOS
        else -> KotlinNativeTargetOs.UNKNOWN
    }
}