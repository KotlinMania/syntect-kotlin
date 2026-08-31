// port-lint: tests syntect/src/parsing/syntax_definition.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.parsing.ContextId
import io.github.kotlinmania.syntect.parsing.Regex
import io.github.kotlinmania.syntect.parsing.Region
import io.github.kotlinmania.syntect.parsing.Scope
import io.github.kotlinmania.syntect.parsing.SyntaxDefinition
import io.github.kotlinmania.syntect.parsing.SyntaxReference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyntaxDefinitionTest {
    @Test
    fun canCompileRefs() {
        val r = Regex.new("(\\\\\\[\\]\\(\\))(b)(c)(d)(e)")
        val s = "\\[]()bcde"
        val region = Region.new()
        val matched = r.search(s, 0, s.length, region)
        assertTrue(matched)
        val pos0 = region.pos(0)
        assertTrue(pos0 != null)
    }

    @Test
    fun testSyntaxDefinitionData() {
        val def =
            SyntaxDefinition(
                name = "Kotlin",
                fileExtensions = listOf("kt", "kts"),
                scope = Scope.new("source.kotlin"),
            )
        assertEquals("Kotlin", def.name)
        assertEquals(listOf("kt", "kts"), def.fileExtensions)
        assertEquals(Scope.new("source.kotlin"), def.scope)

        val ref =
            SyntaxReference(
                name = "Kotlin",
                fileExtensions = listOf("kt", "kts"),
                scope = Scope.new("source.kotlin"),
            )
        assertEquals("Kotlin", ref.name)

        val contextId = ContextId(1, 2)
        assertEquals(1, contextId.syntaxIndex)
        assertEquals(2, contextId.contextIndex)
    }
}
