package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.url

interface UrlFactory {

    fun get(args: List<String>, type: String? = null): String?

}