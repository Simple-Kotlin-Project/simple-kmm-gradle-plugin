package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.pattern

class ListStringPattern<T>(pattern: String) : StringPattern<List<T>>,
    ListPattern<T, String> {

    private val actions: List<StringPatterAction<List<T>>>

    init {
        val actions = ArrayList<StringPatterAction<List<T>>>()
        var match: MatchResult? = PATTERN_ARGUMENT_REGEX.find(pattern)
        var prevIndex = 0

        while (match != null) {
            val start = match.range.first
            if (start - prevIndex > 0) {
                actions.add(ConstantStringPatternAction(pattern.substring(prevIndex, start)))
            }
            prevIndex = match.range.last + 1
            val index = match.groupValues[1].toIntOrNull()
                ?: error("Can not create pattern. Can not use '${match.groupValues[1]}' as index of argument")

            actions.add(InputStringPatterAction(index))
            match = match.next()
        }

        if (prevIndex < pattern.length) {
            actions.add(ConstantStringPatternAction(pattern.substring(prevIndex, pattern.length)))
        }
        this.actions = actions
    }

    override fun pattern(input: List<T>): String =
        actions.joinToString(separator = "") { it.get(input) }

    private interface StringPatterAction<T> {
        fun get(input: T): String
    }

    private class ConstantStringPatternAction<T>(private val string: String) : StringPatterAction<T> {
        override fun get(input: T): String =
            string
    }

    private class InputStringPatterAction<T>(private val index: Int) : StringPatterAction<List<T>> {
        override fun get(input: List<T>): String =
            input[index].toString()
    }

    private companion object {
        val PATTERN_ARGUMENT_REGEX = Regex("\\{(\\d)*}")
    }
}

