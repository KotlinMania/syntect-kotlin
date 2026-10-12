// port-lint: source util.rs
package io.github.kotlinmania.syntect.util

import io.github.kotlinmania.syntect.highlighting.Color
import io.github.kotlinmania.syntect.highlighting.HighlightedSegment
import io.github.kotlinmania.syntect.highlighting.Style
import io.github.kotlinmania.syntect.highlighting.StyleModifier
import io.github.kotlinmania.syntect.parsing.ParseOp
import io.github.kotlinmania.syntect.parsing.ScopeStackOp

/**
 * Blends a foreground color with transparency over a background color.
 */
fun blendFgColor(
    fg: Color,
    bg: Color,
): Color {
    if (fg.a == 0xFFu.toUByte()) {
        return fg
    }
    val ratio = fg.a.toInt()
    val r = (fg.r.toInt() * ratio + bg.r.toInt() * (255 - ratio)) / 255
    val g = (fg.g.toInt() * ratio + bg.g.toInt() * (255 - ratio)) / 255
    val b = (fg.b.toInt() * ratio + bg.b.toInt() * (255 - ratio)) / 255
    return Color(r.toUByte(), g.toUByte(), b.toUByte(), 0xFFu)
}

/**
 * Formats the styled fragments using 24-bit color terminal escape codes.
 */
fun as24BitTerminalEscaped(
    v: List<HighlightedSegment>,
    bg: Boolean = true,
): String {
    val sb = StringBuilder()
    for (segment in v) {
        val style = segment.style
        val text = segment.text
        if (bg) {
            sb.append("\u001B[48;2;${style.background.r};${style.background.g};${style.background.b}m")
        }
        val fg = blendFgColor(style.foreground, style.background)
        sb.append("\u001B[38;2;${fg.r};${fg.g};${fg.b}m")
        sb.append(text)
    }
    return sb.toString()
}

val LATEX_REPLACE: List<Pair<String, String>> =
    listOf(
        Pair("\\", "\\\\"),
        Pair("{", "\\{"),
        Pair("}", "\\}"),
    )

/**
 * Formats the styled fragments using LaTeX textcolor directive.
 *
 * Background color is ignored.
 */
fun asLatexEscaped(v: List<HighlightedSegment>): String {
    val s = StringBuilder()
    var prevStyle: Style? = null
    fun textcolor(style: Style, first: Boolean): String {
        val prefix = if (first) "" else "}"
        return "$prefix\\textcolor[RGB]{${style.foreground.r},${style.foreground.b},${style.foreground.g}}{"
    }
    for (segment in v) {
        val style = segment.style
        val text = segment.text
        if (prevStyle != null) {
            when (text) {
                " " -> {
                    s.append(' ')
                    continue
                }
                "\n" -> continue
                else -> Unit
            }
            if (style != prevStyle) {
                s.append(textcolor(style, false))
            }
        } else {
            s.append(textcolor(style, true))
        }
        var content = text
        for (replacement in LATEX_REPLACE) {
            content = content.replace(replacement.first, replacement.second)
        }
        s.append(content)
        prevStyle = style
    }
    s.append('}')
    return s.toString()
}

/**
 * Print out the various push and pop operations in a list
 * with visual alignment to the line.
 */
fun debugPrintOps(line: String, ops: List<ParseOp>) {
    for (entry in ops) {
        val i = entry.offset
        val op = entry.op
        println(line.trimEnd())
        print("".padEnd(i))
        when (op) {
            is ScopeStackOp.Push -> println("^ +${op.scope}")
            is ScopeStackOp.Pop -> println("^ pop ${op.count}")
            is ScopeStackOp.Clear -> println("^ clear ${op.amount}")
            is ScopeStackOp.Restore -> println("^ restore")
            is ScopeStackOp.Noop -> println("noop")
        }
    }
}

/**
 * An iterator over the lines of a string, including the line endings.
 */
class LinesWithEndings(
    private val input: String,
) : Iterable<String>, Sequence<String> {
    override fun iterator(): Iterator<String> = linesWithEndings(input).iterator()

    companion object {
        fun from(input: String): LinesWithEndings = LinesWithEndings(input)
    }
}

/**
 * Returns a sequence of lines preserving their newline terminators.
 */
fun linesWithEndings(input: String): Sequence<String> =
    sequence {
        if (input.isEmpty()) return@sequence
        var remaining = input
        while (remaining.isNotEmpty()) {
            val index = remaining.indexOf('\n')
            if (index != -1) {
                val line = remaining.substring(0, index + 1)
                yield(line)
                remaining = remaining.substring(index + 1)
            } else {
                yield(remaining)
                remaining = ""
            }
        }
    }

/**
 * The split result of a list of highlighted segments.
 */
data class SplitHighlightedSegments(
    val before: List<HighlightedSegment>,
    val after: List<HighlightedSegment>,
)

/**
 * Splits a list of styled tokens at a character index.
 */
fun splitAt(
    v: List<HighlightedSegment>,
    splitI: Int,
): SplitHighlightedSegments {
    var rest = v
    var restSplitI = splitI
    val before = mutableListOf<HighlightedSegment>()

    for (tok in rest) {
        if (tok.text.length > restSplitI) {
            break
        }
        before.add(tok)
        restSplitI -= tok.text.length
    }
    rest = rest.subList(before.size, rest.size)

    val after = mutableListOf<HighlightedSegment>()
    if (rest.isNotEmpty() && restSplitI > 0) {
        val firstTok = rest[0]
        val sa = firstTok.text.substring(0, restSplitI)
        val sb = firstTok.text.substring(restSplitI)
        before.add(HighlightedSegment(firstTok.style, sa))
        after.add(HighlightedSegment(firstTok.style, sb))
        rest = rest.subList(1, rest.size)
    }
    after.addAll(rest)
    return SplitHighlightedSegments(before, after)
}

/**
 * Modifies part of a highlighted line using a style modifier.
 */
fun modifyRange(
    v: List<HighlightedSegment>,
    range: IntRange,
    modifier: StyleModifier,
): List<HighlightedSegment> {
    val split1 = splitAt(v, range.first)
    val split2 = splitAt(split1.after, range.last - range.first + 1)

    val combined = split1.before.toMutableList()
    combined.addAll(split2.before.map { segment -> HighlightedSegment(segment.style.apply(modifier), segment.text) })
    combined.addAll(split2.after)
    return combined
}
