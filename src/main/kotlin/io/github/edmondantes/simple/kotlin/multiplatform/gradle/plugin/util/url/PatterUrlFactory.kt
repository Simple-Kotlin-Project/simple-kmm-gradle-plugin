package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.url

import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.pattern.ListStringPattern
import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.pattern.Pattern
import org.gradle.api.Action
import java.util.EnumMap

private typealias PatterGroup = Map<Int, Pattern<List<String>, String>>
private typealias MutablePatterGroup = MutableMap<Int, Pattern<List<String>, String>>

class PatterUrlFactory : UrlFactory {

    private val patterns: MutableMap<UrlType, PatterGroup> = EnumMap(UrlType::class.java)
    private var unspecified: PatterGroup? = null

    override fun get(args: List<String>, type: UrlType?): String? {
        val group = type?.let { patterns[it] } ?: unspecified ?: return null
        val pattern = group[args.size] ?: group[-1] ?: return null
        return pattern.pattern(args)
    }

    fun unspecified(block: PatterUrlFactoryGroupBuilder.() -> Unit) {
        val builder = PatterUrlFactoryGroupBuilder()
        block(builder)
        unspecified = builder.builder()
    }

    fun unspecified(block: Action<PatterUrlFactoryGroupBuilder>) {
        val builder = PatterUrlFactoryGroupBuilder()
        block.execute(builder)
        unspecified = builder.builder()
    }

    fun unspecified(type: UrlType) {
        unspecified = patterns[type]
    }

    fun forType(type: UrlType, block: PatterUrlFactoryGroupBuilder.() -> Unit) {
        val builder = PatterUrlFactoryGroupBuilder()
        block(builder)
        patterns[type] = builder.builder()
    }

    fun forType(type: UrlType, block: Action<PatterUrlFactoryGroupBuilder>) {
        val builder = PatterUrlFactoryGroupBuilder()
        block.execute(builder)
        patterns[type] = builder.builder()
    }

    fun forTypeFrom(type: UrlType, from: UrlType) {
        patterns[from]?.let { patterns[type] = it }
    }
}

class PatterUrlFactoryGroupBuilder {

    private val patterns: MutablePatterGroup = HashMap()

    fun pattern(size: Int, pattern: String) {
        patterns[size] = ListStringPattern(pattern)
    }

    fun elsePattern(pattern: String) {
        pattern(-1, pattern)
    }

    fun builder(): PatterGroup = patterns

}