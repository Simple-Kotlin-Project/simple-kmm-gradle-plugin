package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.url

/**
 * This class generate url string from arguments and [UrlType]
 * @see EmptyUrlFactory
 * @see PatterUrlFactory
 * @see UrlParser
 */
interface UrlFactory {

    /**
     * Gets url string from [args] and for [type]
     */
    fun get(args: List<String>, type: UrlType? = null): String?

}