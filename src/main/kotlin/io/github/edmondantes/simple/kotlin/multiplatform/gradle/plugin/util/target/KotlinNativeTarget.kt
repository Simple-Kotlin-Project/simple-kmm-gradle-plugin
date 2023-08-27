package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.target

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.target.KotlinNativeTargetOs.LINUX
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.target.KotlinNativeTargetOs.MACOS
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.target.KotlinNativeTargetOs.WINDOWS

// According to https://kotlinlang.org/docs/native-target-support.html
enum class KotlinNativeTarget(val targetName: String, val tier: Int, val osRunnable: KotlinNativeTargetOs) {
    LINUX_X64("linuxX64", 1, LINUX),
    MACOS_X64("macosX64", 1, MACOS),
    MACOS_ARM64("macosArm64", 1, MACOS),
    IOS_SIMULATOR_ARM64("iosSimulatorArm64", 1, MACOS),
    IOS_X64("iosX64", 1, MACOS),

    LINUX_ARM64("linuxArm64", 2, LINUX),
    WATCHOS_SIMULATOR_ARM64("watchosSimulatorArm64", 2, MACOS),
    WATCHOS_X64("watchosX64", 2, MACOS),
    WATCHOS_ARM32("watchosArm32", 2, MACOS),
    WATCHOS_ARM64("watchosArm64", 2, MACOS),
    TVOS_SIMULATOR_ARM64("tvosSimulatorArm64", 2, MACOS),
    TVOS_X64("tvosX64", 2, MACOS),
    TVOS_ARM64("tvosArm64", 2, MACOS),
    IOS_ARM64("iosArm64", 2, MACOS),

    ANDROID_NATIVE_ARM32("androidNativeArm32", 3, LINUX),
    ANDROID_NATIVE_ARM64("androidNativeArm64", 3, LINUX),
    ANDROID_NATIVE_X86("androidNativeX86", 3, LINUX),
    ANDROID_NATIVE_X64("androidNativeX64", 3, LINUX),
    WATCHOS_DEVICE_ARM64("watchosDeviceArm64", 3, MACOS),
    MINGW_X64("mingwX64", 3, WINDOWS)
}