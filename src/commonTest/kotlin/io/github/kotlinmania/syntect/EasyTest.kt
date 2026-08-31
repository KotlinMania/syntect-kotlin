// port-lint: tests syntect/src/easy.rs
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
    private fun createTestTheme(): Theme =
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
                    ThemeItem(
                        scope = ScopeSelectors.fromString("keyword.operator"),
                        style =
                            StyleModifier(
                                foreground = Color(100u, 255u, 100u, 255u),
                            ),
                    ),
                ),
        )

    @Test
    fun canHighlightLines() {
        val theme = createTestTheme()
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "TestSyntax",
                fileExtensions = listOf("rs", "test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("rs")!!

        val highlighter = HighlightLines(syntax, theme)
        val segments = highlighter.highlightLine("pub struct Wow { hi: u64 }", ss)
        assertTrue(segments.isNotEmpty())
        assertEquals(Color(255u, 100u, 100u, 255u), segments[0].style.foreground)
    }

    @Test
    fun canHighlightFile() {
        val theme = createTestTheme()
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "ERB",
                fileExtensions = listOf("erb"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("erb")!!

        val highlighter = HighlightLines(syntax, theme)
        val segments = highlighter.highlightLine("test line", ss)
        assertTrue(segments.isNotEmpty())
    }

    @Test
    fun canFindRegions() {
        val line = "lol =5+2"
        val ops =
            listOf(
                ParseOp(0, ScopeStackOp.Push(Scope.new("source.ruby"))),
                ParseOp(4, ScopeStackOp.Push(Scope.new("keyword.operator.assignment.ruby"))),
                ParseOp(5, ScopeStackOp.Pop(1)),
                ParseOp(8, ScopeStackOp.Noop),
            )
        val iter = ScopeRegionIterator(ops, line)
        val stack = ScopeStack()
        var tokenCount = 0
        while (iter.hasNext()) {
            val (s, op) = iter.next()
            stack.apply(op)
            if (s.isEmpty()) continue
            if (tokenCount == 1) {
                assertEquals(ScopeStack.fromString("source.ruby keyword.operator.assignment.ruby"), stack)
                assertEquals("=", s)
            }
            tokenCount++
        }
        assertEquals(3, tokenCount)
    }

    @Test
    fun canFindRegionsWithTrailingNewline() {
        val lines = listOf("# hello world\n", "lol=5+2\n")
        val stack = ScopeStack()
        for (line in lines) {
            val ops =
                listOf(
                    ParseOp(0, ScopeStackOp.Push(Scope.new("comment.line"))),
                    ParseOp(line.length, ScopeStackOp.Pop(1)),
                )
            val iteratedOps = mutableListOf<ScopeStackOp>()
            val iter = ScopeRegionIterator(ops, line)
            while (iter.hasNext()) {
                val (_, op) = iter.next()
                stack.apply(op)
                iteratedOps.add(op)
            }
            assertTrue(iteratedOps.isNotEmpty())
        }
    }

    @Test
    fun canStartAgainFromPreviousState() {
        val theme = createTestTheme()
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Python",
                fileExtensions = listOf("py"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("py")!!

        val highlighter = HighlightLines(syntax, theme)
        val lines = listOf("\"\"\"", "def foo():", "\"\"\"")
        val highlightedFirst = highlighter.highlightLine(lines[0], ss)
        assertTrue(highlightedFirst.isNotEmpty())

        val (hState, pState) = highlighter.state()
        val otherHighlighter = HighlightLines.fromState(theme, hState, pState)
        val highlightedSecond = otherHighlighter.highlightLine(lines[1], ss)
        assertTrue(highlightedSecond.isNotEmpty())
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
        assertEquals(IntRange(11, 10), ranges[2])
    }
}
