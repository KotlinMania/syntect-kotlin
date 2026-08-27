// port-lint: source easy.rs
package io.github.kotlinmania.syntect.easy

import io.github.kotlinmania.syntect.highlighting.HighlightIterator
import io.github.kotlinmania.syntect.highlighting.HighlightState
import io.github.kotlinmania.syntect.highlighting.HighlightedSegment
import io.github.kotlinmania.syntect.highlighting.Highlighter
import io.github.kotlinmania.syntect.highlighting.Theme
import io.github.kotlinmania.syntect.parsing.ParseOp
import io.github.kotlinmania.syntect.parsing.ParseState
import io.github.kotlinmania.syntect.parsing.ScopeStack
import io.github.kotlinmania.syntect.parsing.ScopeStackOp
import io.github.kotlinmania.syntect.parsing.SyntaxReference
import io.github.kotlinmania.syntect.parsing.SyntaxSet

/**
 * Simple way to go directly from lines of text to colored tokens.
 */
class HighlightLines(
    val highlighter: Highlighter,
    val parseState: ParseState,
    val highlightState: HighlightState,
) {
    constructor(syntax: SyntaxReference, theme: Theme) : this(
        highlighter = Highlighter(theme),
        parseState = ParseState(syntax),
        highlightState = HighlightState.new(Highlighter(theme), ScopeStack()),
    )

    /**
     * Highlights a single line of text, updating internal state across lines.
     */
    fun highlightLine(
        line: String,
        syntaxSet: SyntaxSet,
    ): List<HighlightedSegment> {
        val ops = parseState.parseLine(line, syntaxSet)
        val iterator = HighlightIterator(highlightState, ops, line, highlighter)
        val result = mutableListOf<HighlightedSegment>()
        while (iterator.hasNext()) {
            result.add(iterator.next())
        }
        return result
    }

/**
     * Combined highlight and parse state.
     */
    data class HighlightLinesState(
        val highlightState: HighlightState,
        val parseState: ParseState,
    )

    /**
     * Returns the current highlight and parse states.
     */
    fun state(): HighlightLinesState = HighlightLinesState(highlightState, parseState)

    companion object {
        /**
         * Starts again from a previous state.
         */
        fun fromState(
            theme: Theme,
            highlightState: HighlightState,
            parseState: ParseState,
        ): HighlightLines =
            HighlightLines(
                highlighter = Highlighter(theme),
                parseState = parseState,
                highlightState = highlightState,
            )
    }
}

private val NOOP_OP: ScopeStackOp = ScopeStackOp.Noop

/**
 * Item yielded by [ScopeRangeIterator]: character range and the operation.
 */
data class ScopeRangeItem(
    val range: IntRange,
    val op: ScopeStackOp,
)

/**
 * Iterator over the ranges of a line to which a given parser operation applies.
 */
class ScopeRangeIterator(
    val ops: List<ParseOp>,
    val line: String,
) : Iterator<ScopeRangeItem> {
    private var index: Int = 0
    private var lastStrIndex: Int = 0

    override fun hasNext(): Boolean = index <= ops.size

    override fun next(): ScopeRangeItem {
        if (!hasNext()) throw NoSuchElementException()

        val nextStrI =
            if (index == ops.size) {
                line.length
            } else {
                ops[index].offset
            }
        val range = lastStrIndex until nextStrI
        lastStrIndex = nextStrI

        val op =
            if (index == 0) {
                NOOP_OP
            } else {
                ops[index - 1].op
            }

        index++
        return ScopeRangeItem(range, op)
    }
}

/**
 * Item yielded by [ScopeRegionIterator]: substring slice and the operation.
 */
data class ScopeRegionItem(
    val text: String,
    val op: ScopeStackOp,
)

/**
 * A convenience wrapper over [ScopeRangeIterator] to return substrings directly.
 */
class ScopeRegionIterator(
    ops: List<ParseOp>,
    val line: String,
) : Iterator<ScopeRegionItem> {
    private val rangeIter = ScopeRangeIterator(ops, line)

    override fun hasNext(): Boolean = rangeIter.hasNext()

    override fun next(): ScopeRegionItem {
        val (range, op) = rangeIter.next()
        val text = line.substring(range.first, range.last + 1)
        return ScopeRegionItem(text, op)
    }
}
