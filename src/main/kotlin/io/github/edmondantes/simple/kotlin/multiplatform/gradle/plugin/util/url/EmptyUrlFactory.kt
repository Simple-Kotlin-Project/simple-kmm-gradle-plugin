package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.url

class EmptyUrlFactory : UrlFactory {
    override fun get(args: List<String>, type: UrlType?): String? =
        null
}