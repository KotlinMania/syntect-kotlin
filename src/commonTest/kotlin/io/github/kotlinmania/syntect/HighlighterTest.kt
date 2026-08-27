// port-lint: tests syntect/src/highlighting/highlighter.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.highlighting.Color
import io.github.kotlinmania.syntect.highlighting.FontStyle
import io.github.kotlinmania.syntect.highlighting.HighlightIterator
import io.github.kotlinmania.syntect.highlighting.HighlightState
import io.github.kotlinmania.syntect.highlighting.HighlightedSegment
import io.github.kotlinmania.syntect.highlighting.Highlighter
import io.github.kotlinmania.syntect.highlighting.RangedHighlightIterator
import io.github.kotlinmania.syntect.highlighting.RangedToken
import io.github.kotlinmania.syntect.highlighting.ScopeSelectors
import io.github.kotlinmania.syntect.highlighting.StyleModifier
import io.github.kotlinmania.syntect.highlighting.Theme
import io.github.kotlinmania.syntect.highlighting.ThemeItem
import io.github.kotlinmania.syntect.highlighting.ThemeSettings
import io.github.kotlinmania.syntect.parsing.ParseOp
import io.github.kotlinmania.syntect.parsing.Scope
import io.github.kotlinmania.syntect.parsing.ScopeStack
import io.github.kotlinmania.syntect.parsing.ScopeStackOp
import kotlin.test.Test
import kotlin.test.assertEquals

class HighlighterTest {
    @Test
    fun testHighlighter() {
        val theme =
            Theme(
                name = "TestTheme",
                settings =
                    ThemeSettings(
                        foreground = Color(200u, 200u, 200u, 255u),
                        background = Color(30u, 30u, 30u, 255u),
                    ),
                scopes =
                    listOf(
                        ThemeItem(
                            scope = ScopeSelectors.fromString("keyword"),
                            style =
                                StyleModifier(
                                    foreground = Color(255u, 100u, 100u, 255u),
                                    fontStyle = FontStyle.BOLD,
                                ),
                        ),
                        ThemeItem(
                            scope = ScopeSelectors.fromString("string"),
                            style =
                                StyleModifier(
                                    foreground = Color(100u, 255u, 100u, 255u),
                                ),
                        ),
                    ),
            )

        val highlighter = Highlighter(theme)
        val defaultStyle = highlighter.getDefault()
        assertEquals(Color(200u, 200u, 200u, 255u), defaultStyle.foreground)
        assertEquals(Color(30u, 30u, 30u, 255u), defaultStyle.background)

        val keywordScope = Scope.new("keyword.control")
        val keywordStyle = highlighter.styleForStack(listOf(keywordScope))
        assertEquals(Color(255u, 100u, 100u, 255u), keywordStyle.foreground)
        assertEquals(FontStyle.BOLD, keywordStyle.fontStyle)

        val stringScope = Scope.new("string.quoted.double")
        val stringStyle = highlighter.styleForStack(listOf(stringScope))
        assertEquals(Color(100u, 255u, 100u, 255u), stringStyle.foreground)
        assertEquals(FontStyle.empty(), stringStyle.fontStyle)
    }

    @Test
    fun canParse() {
        val theme =
            Theme(
                name = "TestTheme",
                settings =
                    ThemeSettings(
                        foreground = Color(200u, 200u, 200u, 255u),
                        background = Color(30u, 30u, 30u, 255u),
                    ),
                scopes =
                    listOf(
                        ThemeItem(
                            scope = ScopeSelectors.fromString("keyword"),
                            style =
                                StyleModifier(
                                    foreground = Color(208u, 135u, 112u, 255u),
                                ),
                        ),
                    ),
            )
        val highlighter = Highlighter(theme)
        val state = HighlightState.new(highlighter, ScopeStack())
        val ops = listOf(ParseOp(0, ScopeStackOp.Push(Scope.new("keyword"))), ParseOp(5, ScopeStackOp.Pop(1)))
        val iter = HighlightIterator(state, ops, "hello", highlighter)
        val regions = mutableListOf<HighlightedSegment>()
        while (iter.hasNext()) {
            regions.add(iter.next())
        }
        assertEquals(1, regions.size)
        assertEquals(Color(208u, 135u, 112u, 255u), regions[0].style.foreground)
    }

    @Test
    fun canParseWithHighlightStateFromCache() {
        val theme =
            Theme(
                name = "TestTheme",
                settings =
                    ThemeSettings(
                        foreground = Color(200u, 200u, 200u, 255u),
                        background = Color(30u, 30u, 30u, 255u),
                    ),
                scopes =
                    listOf(
                        ThemeItem(
                            scope = ScopeSelectors.fromString("comment"),
                            style =
                                StyleModifier(
                                    foreground = Color(101u, 115u, 126u, 255u),
                                ),
                        ),
                    ),
            )
        val highlighter = Highlighter(theme)
        val state = HighlightState.new(highlighter, ScopeStack())
        val ops = listOf(ParseOp(0, ScopeStackOp.Push(Scope.new("comment"))), ParseOp(17, ScopeStackOp.Pop(1)))
        val iter = HighlightIterator(state, ops, "multiline comment", highlighter)
        val regions = mutableListOf<HighlightedSegment>()
        while (iter.hasNext()) {
            regions.add(iter.next())
        }
        assertEquals(1, regions.size)
        assertEquals(Color(101u, 115u, 126u, 255u), regions[0].style.foreground)
    }

    @Test
    fun trickyCases() {
        val c1 = Color(1u, 1u, 1u, 255u)
        val theme =
            Theme(
                scopes =
                    listOf(
                        ThemeItem(
                            scope = ScopeSelectors.fromString("comment.line"),
                            style = StyleModifier(foreground = c1),
                        ),
                    ),
            )
        val highlighter = Highlighter(theme)
        val state = HighlightState.new(highlighter, ScopeStack())
        val ops = listOf(ParseOp(0, ScopeStackOp.Push(Scope.new("comment.line.rs"))), ParseOp(6, ScopeStackOp.Pop(1)))
        val iter = HighlightIterator(state, ops, "abcdef", highlighter)
        val regions = mutableListOf<HighlightedSegment>()
        while (iter.hasNext()) {
            regions.add(iter.next())
        }
        assertEquals(1, regions.size)
        assertEquals(c1, regions[0].style.foreground)
    }

    @Test
    fun testRanges() {
        val theme =
            Theme(
                name = "RangedTheme",
                settings =
                    ThemeSettings(
                        foreground = Color(255u, 255u, 255u, 255u),
                        background = Color(0u, 0u, 0u, 255u),
                    ),
                scopes = emptyList(),
            )
        val highlighter = Highlighter(theme)
        val state = HighlightState.new(highlighter, ScopeStack())
        val line = "test range"
        val ops = listOf(ParseOp(4, ScopeStackOp.Noop), ParseOp(10, ScopeStackOp.Noop))

        val iterator = RangedHighlightIterator(state, ops, line, highlighter)
        val tokens = mutableListOf<RangedToken>()
        while (iterator.hasNext()) {
            tokens.add(iterator.next())
        }

        assertEquals(2, tokens.size)
        assertEquals("test", tokens[0].text)
        assertEquals(0 until 4, tokens[0].range)
        assertEquals(" range", tokens[1].text)
        assertEquals(4 until 10, tokens[1].range)
    }

    @Test
    fun testHighlightIterator() {
        val theme =
            Theme(
                name = "IteratorTheme",
                settings =
                    ThemeSettings(
                        foreground = Color(255u, 255u, 255u, 255u),
                        background = Color(0u, 0u, 0u, 255u),
                    ),
                scopes =
                    listOf(
                        ThemeItem(
                            scope = ScopeSelectors.fromString("keyword"),
                            style =
                                StyleModifier(
                                    foreground = Color(208u, 135u, 112u, 255u),
                                ),
                        ),
                    ),
            )

        val highlighter = Highlighter(theme)
        val state = HighlightState.new(highlighter, ScopeStack())
        val line = "val x = 5"
        val ops =
            listOf(
                ParseOp(0, ScopeStackOp.Push(Scope.new("keyword"))),
                ParseOp(3, ScopeStackOp.Pop(1)),
                ParseOp(9, ScopeStackOp.Noop),
            )

        val iterator = HighlightIterator(state, ops, line, highlighter)
        val tokens = mutableListOf<HighlightedSegment>()
        while (iterator.hasNext()) {
            tokens.add(iterator.next())
        }

        assertEquals(2, tokens.size)
        assertEquals("val", tokens[0].text)
        assertEquals(Color(208u, 135u, 112u, 255u), tokens[0].style.foreground)
        assertEquals(" x = 5", tokens[1].text)
        assertEquals(Color(255u, 255u, 255u, 255u), tokens[1].style.foreground)
    }
}
