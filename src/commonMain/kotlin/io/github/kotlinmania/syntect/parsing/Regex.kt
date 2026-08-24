// port-lint: source parsing/regex.rs
package io.github.kotlinmania.syntect.parsing

/**
 * A start and end position of a match or capture group.
 */
data class RegionPos(
    val start: Int,
    val end: Int,
)

/**
 * Start and end text positions in a region.
 */
data class RegionPosition(
    val start: Int,
    val end: Int,
)

/**
 * A region contains text positions for capture groups in a match result.
 */
class Region {
    private val startPositions = mutableListOf<Int>()
    private val endPositions = mutableListOf<Int>()

    /**
     * Get the start/end positions of the capture group with given index.
     * Index 0 returns the whole match.
     */
    fun pos(index: Int): RegionPosition? =
        if (index in 0 until startPositions.size && startPositions[index] >= 0) {
            RegionPosition(startPositions[index], endPositions[index])
        } else {
            null
        }

    fun initFromMatchResult(matchResult: MatchResult, text: String) {
        startPositions.clear()
        endPositions.clear()
        val wholeRange = matchResult.range
        startPositions.add(wholeRange.first)
        endPositions.add(wholeRange.last + 1)
        val searchStart = wholeRange.first
        for (i in 1 until matchResult.groupValues.size) {
            val valStr = matchResult.groupValues[i]
            if (valStr.isNotEmpty()) {
                val idx = text.indexOf(valStr, searchStart)
                if (idx in wholeRange.first..wholeRange.last) {
                    startPositions.add(idx)
                    endPositions.add(idx + valStr.length)
                } else {
                    startPositions.add(-1)
                    endPositions.add(-1)
                }
            } else {
                startPositions.add(-1)
                endPositions.add(-1)
            }
        }
    }

    fun clear() {
        startPositions.clear()
        endPositions.clear()
    }

    companion object {
        fun new(): Region = Region()
    }
}

/**
 * An abstraction for regex patterns.
 * Lazily compiles regexes on first use.
 */
class Regex(
    val regexStr: String,
) {
    private var compiledRegex: kotlin.text.Regex? = null

    private fun getOrCompile(): kotlin.text.Regex {
        val r = compiledRegex
        if (r != null) return r
        val newRegex = kotlin.text.Regex(regexStr)
        compiledRegex = newRegex
        return newRegex
    }

    /**
     * Check if the regex matches the given text.
     */
    fun isMatch(text: String): Boolean = getOrCompile().containsMatchIn(text)

    /**
     * Search for the pattern in the given text from begin/end positions.
     */
    fun search(
        text: String,
        begin: Int,
        end: Int,
        region: Region? = null,
    ): Boolean {
        val sliced = text.substring(0, minOf(end, text.length))
        val matchResult = getOrCompile().find(sliced, begin)
        return if (matchResult != null) {
            region?.initFromMatchResult(matchResult, text)
            true
        } else {
            false
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Regex) return false
        return regexStr == other.regexStr
    }

    override fun hashCode(): Int = regexStr.hashCode()

    override fun toString(): String = regexStr

    companion object {
        fun new(regexStr: String): Regex = Regex(regexStr)

        fun tryCompile(regexStr: String): Throwable? =
            try {
                kotlin.text.Regex(regexStr)
                null
            } catch (e: Throwable) {
                e
            }
    }
}
