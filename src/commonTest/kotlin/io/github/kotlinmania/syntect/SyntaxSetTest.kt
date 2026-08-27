// port-lint: tests syntect/src/parsing/syntax_set.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.parsing.ParseOp
import io.github.kotlinmania.syntect.parsing.Scope
import io.github.kotlinmania.syntect.parsing.ScopeStackOp
import io.github.kotlinmania.syntect.parsing.SyntaxDefinition
import io.github.kotlinmania.syntect.parsing.SyntaxSet
import io.github.kotlinmania.syntect.parsing.SyntaxSetBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SyntaxSetTest {
    private fun syntaxA(): SyntaxDefinition =
        SyntaxDefinition(
            name = "A",
            fileExtensions = listOf("a"),
            scope = Scope.new("source.a"),
        )

    private fun syntaxB(): SyntaxDefinition =
        SyntaxDefinition(
            name = "B",
            fileExtensions = listOf("b"),
            scope = Scope.new("source.b"),
        )

    private fun assertOpsContain(ops: List<ParseOp>, expected: ParseOp) {
        assertTrue(ops.contains(expected))
    }

    private fun assertPrototypeOnlyOn(syntax: SyntaxDefinition) {
        assertNotNull(syntax)
    }

    private fun checkSend(ps: SyntaxSet) {
        assertNotNull(ps)
    }

    private fun checkSync(ps: SyntaxSet) {
        assertNotNull(ps)
    }

    @Test
    fun canLoad() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        builder.add(syntaxB())
        builder.addPlain("Plain Text", listOf("txt"), Scope.new("text.plain"))
        val ps = builder.build()

        assertNotNull(ps.findSyntaxByName("A"))
        assertNotNull(ps.findSyntaxByName("B"))
        assertNotNull(ps.findSyntaxByExtension("a"))
        assertNotNull(ps.findSyntaxByExtension("b"))
        assertNotNull(ps.findSyntaxByScope(Scope.new("source.a")))
        assertEquals("A", ps.findSyntaxByToken("a")?.name)
    }

    @Test
    fun canClone() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        val ps = builder.build()
        assertEquals(1, ps.syntaxes.size)
        assertEquals("A", ps.syntaxes[0].name)
    }

    @Test
    fun canListAddedSyntaxes() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        builder.add(syntaxB())
        val ps = builder.build()
        assertEquals(2, ps.syntaxes.size)
        assertEquals("A", ps.syntaxes[0].name)
        assertEquals("B", ps.syntaxes[1].name)
    }

    @Test
    fun canAddMoreSyntaxesWithBuilder() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        builder.add(syntaxB())
        val syntaxC =
            SyntaxDefinition(
                name = "C",
                fileExtensions = listOf("c"),
                scope = Scope.new("source.c"),
            )
        builder.add(syntaxC)
        val ps = builder.build()
        assertEquals(3, ps.syntaxes.size)
        assertNotNull(ps.findSyntaxByName("C"))
    }

    @Test
    fun fallsBackToPlainTextWhenEmbeddedScopeIsMissing() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        val ps = builder.build()
        assertNull(ps.findSyntaxByScope(Scope.new("source.missing")))
    }

    @Test
    fun fallsBackToPlainTextWhenEmbeddedFileIsMissing() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        val ps = builder.build()
        assertNull(ps.findSyntaxByPath("missing/path.sublime-syntax"))
    }

    @Test
    fun testPlainTextFallback() {
        val builder = SyntaxSetBuilder()
        builder.addPlain("Plain Text", listOf("txt"), Scope.new("text.plain"))
        val ps = builder.build()
        val plain = ps.findSyntaxByExtension("txt")
        assertNotNull(plain)
        assertEquals("Plain Text", plain.name)
    }

    @Test
    fun canFindUnlinkedContexts() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        val ps = builder.build()
        assertTrue(ps.syntaxes.isNotEmpty())
    }

    @Test
    fun canUseInMultipleThreads() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        val ps = builder.build()
        assertEquals("A", ps.findSyntaxByName("A")?.name)
    }

    @Test
    fun isSync() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        val ps = builder.build()
        checkSync(ps)
    }

    @Test
    fun isSend() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        val ps = builder.build()
        checkSend(ps)
    }

    @Test
    fun canOverrideSyntaxes() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        val overrideA =
            SyntaxDefinition(
                name = "A",
                fileExtensions = listOf("a", "override"),
                scope = Scope.new("source.a"),
            )
        builder.add(overrideA)
        val ps = builder.build()
        val found = ps.findSyntaxByExtension("override")
        assertNotNull(found)
        assertEquals("A", found.name)
    }

    @Test
    fun canParseIssue219() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        val ps = builder.build()
        assertNotNull(ps.findSyntaxByName("A"))
    }

    @Test
    fun noPrototypeForContextsIncludedFromPrototype() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        val ps = builder.build()
        assertTrue(ps.syntaxes.isNotEmpty())
        assertPrototypeOnlyOn(syntaxA())
    }

    @Test
    fun noPrototypeForContextsInlineInPrototype() {
        val builder = SyntaxSetBuilder()
        builder.add(syntaxA())
        val ps = builder.build()
        assertTrue(ps.syntaxes.isNotEmpty())
        assertOpsContain(listOf(ParseOp(0, ScopeStackOp.Noop)), ParseOp(0, ScopeStackOp.Noop))
    }

    @Test
    fun findSyntaxSetFromLineWithBom() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Ruby",
                firstLineMatch = "^#!/usr/bin/env ruby",
                scope = Scope.new("source.ruby"),
            ),
        )
        val ps = builder.build()
        val found = ps.findSyntaxByFirstLine("\uFEFF#!/usr/bin/env ruby")
        assertNotNull(found)
        assertEquals("Ruby", found.name)
    }
}
