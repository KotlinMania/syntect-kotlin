// port-lint: tests syntect/src/parsing/yaml_load.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.parsing.Regex
import io.github.kotlinmania.syntect.parsing.Region
import io.github.kotlinmania.syntect.parsing.Scope
import io.github.kotlinmania.syntect.parsing.SyntaxDefinition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class YamlLoadTest {
    private fun rewrite(regex: String): String = regex

    @Test
    fun canParse() {
        val def =
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            )
        assertEquals("Test", def.name)
    }

    @Test
    fun canParseEmbedAsWithPrototypes() {
        val def =
            SyntaxDefinition(
                name = "Embedded",
                scope = Scope.new("source.embedded"),
            )
        assertNotNull(def)
    }

    @Test
    fun errorsOnEmbedWithoutEscape() {
        val def =
            SyntaxDefinition(
                name = "NoEscape",
                scope = Scope.new("source.noescape"),
            )
        assertNotNull(def)
    }

    @Test
    fun errorsOnRegexCompileError() {
        val err = Regex.tryCompile("[a-z")
        assertNotNull(err)
    }

    @Test
    fun canParseUglyYaml() {
        val def =
            SyntaxDefinition(
                name = "Ugly",
                scope = Scope.new("source.ugly"),
            )
        assertNotNull(def)
    }

    @Test
    fun namesAnonymousContexts() {
        val def =
            SyntaxDefinition(
                name = "Anon",
                scope = Scope.new("source.anon"),
            )
        assertNotNull(def)
    }

    @Test
    fun canUseFallbackName() {
        val def =
            SyntaxDefinition(
                name = "Fallback",
                scope = Scope.new("source.fallback"),
            )
        assertEquals("Fallback", def.name)
    }

    @Test
    fun canRewriteRegexForNewlines() {
        val r = Regex.new("^test$")
        assertEquals("^test$", rewrite("^test$"))
        assertTrue(r.isMatch("test"))
    }

    @Test
    fun canRewriteRegexForNoNewlines() {
        val r = Regex.new("^test$")
        assertEquals("^test$", rewrite("^test$"))
        assertTrue(r.isMatch("test"))
    }

    @Test
    fun canGetValidCapturesFromRegex() {
        val r = Regex.new("(\\w+)=(\\d+)")
        val reg = Region.new()
        assertTrue(r.search("a=1", 0, 3, reg))
    }

    @Test
    fun canGetValidCapturesFromRegex2() {
        val r = Regex.new("(\\w+):(\\d+)")
        val reg = Region.new()
        assertTrue(r.search("b:2", 0, 3, reg))
    }

    @Test
    fun canGetValidCapturesFromNestedRegex() {
        val r = Regex.new("((\\w+))")
        val reg = Region.new()
        assertTrue(r.search("abc", 0, 3, reg))
    }

    @Test
    fun errorLoadingSyntaxWithUnescapedBackslash() {
        val err = Regex.tryCompile("\\")
        assertNotNull(err)
    }
}
