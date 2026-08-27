// port-lint: source syntect/src/util.rs
package io.github.kotlinmania.syntect.util

import io.github.kotlinmania.syntect.highlighting.Color
import io.github.kotlinmania.syntect.highlighting.HighlightedSegment
import io.github.kotlinmania.syntect.highlighting.StyleModifier

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
