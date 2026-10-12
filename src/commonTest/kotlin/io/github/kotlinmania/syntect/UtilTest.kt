// port-lint: tests util.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.highlighting.Color
import io.github.kotlinmania.syntect.highlighting.FontStyle
import io.github.kotlinmania.syntect.highlighting.HighlightedSegment
import io.github.kotlinmania.syntect.highlighting.Style
import io.github.kotlinmania.syntect.highlighting.StyleModifier
import io.github.kotlinmania.syntect.util.LinesWithEndings
import io.github.kotlinmania.syntect.util.as24BitTerminalEscaped
import io.github.kotlinmania.syntect.util.asLatexEscaped
import io.github.kotlinmania.syntect.util.blendFgColor
import io.github.kotlinmania.syntect.util.debugPrintOps
import io.github.kotlinmania.syntect.util.linesWithEndings
import io.github.kotlinmania.syntect.util.modifyRange
import io.github.kotlinmania.syntect.util.splitAt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UtilTest {
    @Test
    fun testLinesWithEndings() {
        fun lines(s: String): List<String> = linesWithEndings(s).toList()

        assertTrue(lines("").isEmpty())
        assertEquals(listOf("f"), lines("f"))
        assertEquals(listOf("foo"), lines("foo"))
        assertEquals(listOf("foo\n"), lines("foo\n"))
        assertEquals(listOf("foo\n", "bar"), lines("foo\nbar"))
        assertEquals(listOf("foo\n", "bar\n"), lines("foo\nbar\n"))
        assertEquals(listOf("foo\r\n", "bar"), lines("foo\r\nbar"))
        assertEquals(listOf("foo\r\n", "bar\r\n"), lines("foo\r\nbar\r\n"))
        assertEquals(listOf("\n", "foo"), lines("\nfoo"))
        assertEquals(listOf("\n", "\n", "\n"), lines("\n\n\n"))
    }

    @Test
    fun testSplitAt() {
        val plain = Style.default()
        val boldMod =
            StyleModifier(
                fontStyle = FontStyle.BOLD,
            )
        val bold = plain.apply(boldMod)

        val l = listOf(HighlightedSegment(plain, "abc"), HighlightedSegment(plain, "def"), HighlightedSegment(plain, "ghi"))
        val split = splitAt(l, 4)
        assertEquals(listOf(HighlightedSegment(plain, "abc"), HighlightedSegment(plain, "d")), split.before)
        assertEquals(listOf(HighlightedSegment(plain, "ef"), HighlightedSegment(plain, "ghi")), split.after)

        val modified = modifyRange(l, 1..5, boldMod)
        assertEquals(
            listOf(
                HighlightedSegment(plain, "a"),
                HighlightedSegment(bold, "bc"),
                HighlightedSegment(bold, "def"),
                HighlightedSegment(plain, "ghi"),
            ),
            modified,
        )
    }

    @Test
    fun testAs24BitTerminalEscaped() {
        val style =
            Style(
                foreground = Color(255u, 0u, 0u, 255u),
                background = Color(0u, 0u, 0u, 255u),
            )
        val escaped = as24BitTerminalEscaped(listOf(HighlightedSegment(style, "test")), bg = true)
        assertTrue(escaped.contains("\u001B[48;2;0;0;0m"))
        assertTrue(escaped.contains("\u001B[38;2;255;0;0m"))
        assertTrue(escaped.contains("test"))
    }

    @Test
    fun testBlendFgColor() {
        val opaqueFg = Color(255u, 0u, 0u, 255u)
        val bg = Color(0u, 0u, 255u, 255u)
        assertEquals(opaqueFg, blendFgColor(opaqueFg, bg))

        val halfAlphaFg = Color(255u, 0u, 0u, 127u)
        val blended = blendFgColor(halfAlphaFg, bg)
        assertEquals(255u.toUByte(), blended.a)
    }

    @Test
    fun testSplitAtBoundaryCases() {
        val plain = Style.default()
        val empty = emptyList<HighlightedSegment>()
        val splitEmpty = splitAt(empty, 0)
        assertEquals(emptyList(), splitEmpty.before)
        assertEquals(emptyList(), splitEmpty.after)

        val l = listOf(HighlightedSegment(plain, "abc"), HighlightedSegment(plain, "def"), HighlightedSegment(plain, "ghi"))

        val splitStart = splitAt(l, 0)
        assertEquals(emptyList(), splitStart.before)
        assertEquals(l, splitStart.after)

        val splitBetween = splitAt(l, 3)
        assertEquals(listOf(HighlightedSegment(plain, "abc")), splitBetween.before)
        assertEquals(listOf(HighlightedSegment(plain, "def"), HighlightedSegment(plain, "ghi")), splitBetween.after)

        val splitEnd = splitAt(l, 9)
        assertEquals(l, splitEnd.before)
        assertEquals(emptyList(), splitEnd.after)

        val splitOutOfBounds = splitAt(l, 10)
        assertEquals(l, splitOutOfBounds.before)
        assertEquals(emptyList(), splitOutOfBounds.after)
    }

    @Test
    fun testLinesWithEndingsClass() {
        val lines = LinesWithEndings.from("foo\nbar\nbaz").toList()
        assertEquals(listOf("foo\n", "bar\n", "baz"), lines)
    }

    @Test
    fun testAsLatexEscaped() {
        val style1 =
            Style(
                foreground = Color(255u, 100u, 50u, 255u),
                background = Color(0u, 0u, 0u, 255u),
            )
        val style2 =
            Style(
                foreground = Color(10u, 20u, 30u, 255u),
                background = Color(0u, 0u, 0u, 255u),
            )

        val single = listOf(HighlightedSegment(style1, "hello {world}\\"))
        val escapedSingle = io.github.kotlinmania.syntect.util.asLatexEscaped(single)
        assertEquals("\\textcolor[RGB]{255,50,100}{hello \\{world\\}\\\\}", escapedSingle)

        val multi =
            listOf(
                HighlightedSegment(style1, "fn "),
                HighlightedSegment(style2, "main()"),
            )
        val escapedMulti = io.github.kotlinmania.syntect.util.asLatexEscaped(multi)
        assertEquals(
            "\\textcolor[RGB]{255,50,100}{fn }\\textcolor[RGB]{10,30,20}{main()}",
            escapedMulti,
        )
    }

    @Test
    fun testDebugPrintOps() {
        val ops =
            listOf(
                io.github.kotlinmania.syntect.parsing.ParseOp(
                    0,
                    io.github.kotlinmania.syntect.parsing.ScopeStackOp.Push(
                        io.github.kotlinmania.syntect.parsing.Scope.new("source.kotlin"),
                    ),
                ),
                io.github.kotlinmania.syntect.parsing.ParseOp(
                    4,
                    io.github.kotlinmania.syntect.parsing.ScopeStackOp.Pop(1),
                ),
            )
        io.github.kotlinmania.syntect.util.debugPrintOps("val x = 1", ops)
    }
}
