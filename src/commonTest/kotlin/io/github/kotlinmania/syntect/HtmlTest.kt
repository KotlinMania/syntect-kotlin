// port-lint: tests syntect/src/html.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.highlighting.Color
import io.github.kotlinmania.syntect.highlighting.FontStyle
import io.github.kotlinmania.syntect.highlighting.HighlightedSegment
import io.github.kotlinmania.syntect.highlighting.ScopeSelectors
import io.github.kotlinmania.syntect.highlighting.Style
import io.github.kotlinmania.syntect.highlighting.StyleModifier
import io.github.kotlinmania.syntect.highlighting.Theme
import io.github.kotlinmania.syntect.highlighting.ThemeItem
import io.github.kotlinmania.syntect.highlighting.ThemeSettings
import io.github.kotlinmania.syntect.html.ClassStyle
import io.github.kotlinmania.syntect.html.ClassedHTMLGenerator
import io.github.kotlinmania.syntect.html.IncludeBackground
import io.github.kotlinmania.syntect.html.cssForThemeWithClassStyle
import io.github.kotlinmania.syntect.html.highlightedHtmlForString
import io.github.kotlinmania.syntect.html.lineTokensToClassedSpans
import io.github.kotlinmania.syntect.html.styledLineToHighlightedHtml
import io.github.kotlinmania.syntect.parsing.ParseOp
import io.github.kotlinmania.syntect.parsing.Scope
import io.github.kotlinmania.syntect.parsing.ScopeStack
import io.github.kotlinmania.syntect.parsing.ScopeStackOp
import io.github.kotlinmania.syntect.parsing.SyntaxDefinition
import io.github.kotlinmania.syntect.parsing.SyntaxSetBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HtmlTest {
    @Test
    fun tokens() {
        val line = "val x"
        val ops =
            listOf(
                ParseOp(0, ScopeStackOp.Push(Scope.new("keyword.declaration"))),
                ParseOp(3, ScopeStackOp.Pop(1)),
                ParseOp(5, ScopeStackOp.Noop),
            )
        val stack = ScopeStack()
        val (html, delta) = lineTokensToClassedSpans(line, ops, ClassStyle.Spaced, stack)
        assertEquals("<span class=\"keyword declaration\">val</span> x", html)
        assertEquals(0, delta)
    }

    @Test
    fun strings() {
        val theme =
            Theme(
                name = "TestTheme",
                settings =
                    ThemeSettings(
                        foreground = Color(255u, 255u, 255u, 255u),
                        background = Color(0u, 0u, 0u, 255u),
                    ),
                scopes = emptyList(),
            )
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

        val html = highlightedHtmlForString("hello\nworld", ss, syntax, theme)
        assertTrue(html.startsWith("<pre style=\"background-color:#000000;\">\n"))
        assertTrue(html.endsWith("</pre>\n"))
    }

    @Test
    fun trickyTestSyntax() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "TestSyntax",
                fileExtensions = listOf("testsyntax"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("testsyntax")!!
        assertTrue(syntax.fileExtensions.contains("testsyntax"))
    }

    @Test
    fun testClassedHtmlGeneratorDoesntPanic() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "JSON",
                fileExtensions = listOf("json"),
                scope = Scope.new("source.json"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("json")!!

        val generator = ClassedHTMLGenerator.newWithClassStyle(syntax, ss, ClassStyle.Spaced)
        generator.parseHtmlForLineWhichIncludesNewline("{\n")
        generator.parseHtmlForLineWhichIncludesNewline("    \"headers\": [\"Number\"]\n")
        generator.parseHtmlForLineWhichIncludesNewline("}\n")
        val html = generator.finalize()
        assertTrue(html.isNotEmpty())
    }

    @Test
    fun testClassedHtmlGenerator() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "R",
                fileExtensions = listOf("r"),
                scope = Scope.new("source.r"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("r")!!

        val generator = ClassedHTMLGenerator.newWithClassStyle(syntax, ss, ClassStyle.Spaced)
        generator.parseHtmlForLineWhichIncludesNewline("x + y\n")
        val html = generator.finalize()
        assertTrue(html.contains("x + y\n"))
    }

    @Test
    fun testClassedHtmlGeneratorPrefixed() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "R",
                fileExtensions = listOf("r"),
                scope = Scope.new("source.r"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("r")!!

        val generator =
            ClassedHTMLGenerator.newWithClassStyle(
                syntax,
                ss,
                ClassStyle.SpacedPrefixed("foo-"),
            )
        generator.parseHtmlForLineWhichIncludesNewline("x + y\n")
        val html = generator.finalize()
        assertTrue(html.contains("x + y\n"))
    }

    @Test
    fun testClassedHtmlGeneratorNoEmptySpan() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Rust",
                fileExtensions = listOf("rs"),
                scope = Scope.new("source.rust"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("rs")!!

        val generator = ClassedHTMLGenerator.newWithClassStyle(syntax, ss, ClassStyle.Spaced)
        generator.parseHtmlForLineWhichIncludesNewline("fn main() {}\n")
        val html = generator.finalize()
        assertTrue(html.contains("fn main() {}\n"))
    }

    @Test
    fun testEscapeCssIdentifier() {
        val theme =
            Theme(
                name = "TestTheme",
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
                                    fontStyle = FontStyle.BOLD,
                                ),
                        ),
                    ),
            )

        val css = cssForThemeWithClassStyle(theme, ClassStyle.Spaced)
        assertTrue(css.contains(".code {"))
        assertTrue(css.contains(".keyword {"))
    }

    @Test
    fun testCssForThemeWithClassStyleIssue308() {
        val theme =
            Theme(
                name = "Solarized (dark)",
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

        val css = cssForThemeWithClassStyle(theme, ClassStyle.Spaced)
        assertFalse(css.isEmpty())
    }

    @Test
    fun testStyledLineToHighlightedHtml() {
        val style1 =
            Style(
                foreground = Color(208u, 135u, 112u, 255u),
                background = Color(43u, 48u, 59u, 255u),
                fontStyle = FontStyle.BOLD,
            )
        val style2 =
            Style(
                foreground = Color(192u, 197u, 206u, 255u),
                background = Color(43u, 48u, 59u, 255u),
                fontStyle = FontStyle.empty(),
            )
        val segments =
            listOf(
                HighlightedSegment(style1, "val "),
                HighlightedSegment(style2, "x = 5"),
            )

        val html = styledLineToHighlightedHtml(segments, IncludeBackground.No)
        assertEquals(
            "<span style=\"font-weight:bold;color:#d08770;\">val </span><span style=\"color:#c0c5ce;\">x = 5</span>",
            html,
        )
    }
}
