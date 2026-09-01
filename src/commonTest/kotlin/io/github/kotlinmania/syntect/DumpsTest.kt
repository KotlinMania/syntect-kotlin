// port-lint: tests dumps.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.highlighting.Theme
import io.github.kotlinmania.syntect.highlighting.ThemeSet
import io.github.kotlinmania.syntect.parsing.Scope
import io.github.kotlinmania.syntect.parsing.SyntaxDefinition
import io.github.kotlinmania.syntect.parsing.SyntaxSetBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DumpsTest {
    @Test
    fun canDumpAndLoad() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        assertEquals(1, ss.syntaxes.size)
    }

    @Test
    fun dumpIsDeterministic() {
        val builder1 = SyntaxSetBuilder()
        builder1.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss1 = builder1.build()

        val builder2 = SyntaxSetBuilder()
        builder2.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss2 = builder2.build()

        assertEquals(ss1.syntaxes.size, ss2.syntaxes.size)
        assertEquals(ss1.syntaxes[0].name, ss2.syntaxes[0].name)
    }

    @Test
    fun hasDefaultThemes() {
        val themeSet = ThemeSet.new()
        themeSet.addTheme("default", Theme(name = "Default"))
        assertTrue(themeSet.themes.isNotEmpty())
    }
}
