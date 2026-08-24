// port-lint: tests easy.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.easy.HighlightLines
import io.github.kotlinmania.syntect.easy.ScopeRangeIterator
import io.github.kotlinmania.syntect.easy.ScopeRegionIterator
import io.github.kotlinmania.syntect.highlighting.Color
import io.github.kotlinmania.syntect.highlighting.ScopeSelectors
import io.github.kotlinmania.syntect.highlighting.StyleModifier
import io.github.kotlinmania.syntect.highlighting.Theme
import io.github.kotlinmania.syntect.highlighting.ThemeItem
import io.github.kotlinmania.syntect.highlighting.ThemeSettings
import io.github.kotlinmania.syntect.parsing.ParseOp
import io.github.kotlinmania.syntect.parsing.Scope
import io.github.kotlinmania.syntect.parsing.ScopeStack
import io.github.kotlinmania.syntect.parsing.ScopeStackOp
import io.github.kotlinmania.syntect.parsing.SyntaxDefinition
import io.github.kotlinmania.syntect.parsing.SyntaxSetBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EasyTest {
    @Test
    fun testHighlightLines() {
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
                            scope = ScopeSelectors.fromString("source.test"),
                            style =
                                StyleModifier(
                                    foreground = Color(255u, 100u, 100u, 255u),
                                ),
                        ),
                    ),
            )

        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "TestSyntax",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!

        val highlighter = HighlightLines(syntax, theme)
        val segments = highlighter.highlightLine("hello world", ss)
        assertTrue(segments.isNotEmpty())
        assertEquals(Color(255u, 100u, 100u, 255u), segments[0].style.foreground)
    }

    @Test
    fun testScopeRangeIterator() {
        val line = "hello world"
        val ops =
            listOf(
                ParseOp(5, ScopeStackOp.Push(Scope.new("keyword"))),
                ParseOp(11, ScopeStackOp.Pop(1)),
            )
        val iter = ScopeRangeIterator(ops, line)
        val ranges = mutableListOf<IntRange>()
        while (iter.hasNext()) {
            val item = iter.next()
            ranges.add(item.range)
        }
        assertEquals(3, ranges.size)
        assertEquals(0 until 5, ranges[0])
        assertEquals(5 until 11, ranges[1])
        assertEquals(11 until 11, ranges[2])
    }

    @Test
    fun testScopeRegionIterator() {
        val line = "lol =5+2"
        val ops =
            listOf(
                ParseOp(4, ScopeStackOp.Push(Scope.new("source.ruby keyword.operator"))),
                ParseOp(5, ScopeStackOp.Pop(1)),
                ParseOp(8, ScopeStackOp.Noop),
            )
        val iter = ScopeRegionIterator(ops, line)
        val regions = mutableListOf<String>()
        val stack = ScopeStack()
        while (iter.hasNext()) {
            val (text, op) = iter.next()
            stack.apply(op)
            if (text.isNotEmpty()) {
                regions.add(text)
            }
        }
        assertEquals(listOf("lol ", "=", "5+2"), regions)
    }
}
