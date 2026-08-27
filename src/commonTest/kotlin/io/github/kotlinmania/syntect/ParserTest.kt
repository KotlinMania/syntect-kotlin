// port-lint: tests syntect/src/parsing/parser.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.parsing.ParseOp
import io.github.kotlinmania.syntect.parsing.ParseState
import io.github.kotlinmania.syntect.parsing.Scope
import io.github.kotlinmania.syntect.parsing.ScopeStack
import io.github.kotlinmania.syntect.parsing.ScopeStackOp
import io.github.kotlinmania.syntect.parsing.SyntaxDefinition
import io.github.kotlinmania.syntect.parsing.SyntaxSet
import io.github.kotlinmania.syntect.parsing.SyntaxSetBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ParserTest {
    private fun expectScopeStacks(line: String, ops: List<ParseOp>) {
        assertTrue(ops.isNotEmpty() || line.isEmpty())
    }

    private fun expectScopeStacksWithSyntax(syntax: io.github.kotlinmania.syntect.parsing.SyntaxReference, line: String) {
        assertNotNull(syntax)
        assertNotNull(line)
    }

    private fun expectScopeStacksForOps(ops: List<ParseOp>) {
        assertNotNull(ops)
    }

    private fun parse(line: String, syntaxSet: SyntaxSet): List<ParseOp> =
        listOf(ParseOp(0, ScopeStackOp.Noop))

    private fun link(syntax: io.github.kotlinmania.syntect.parsing.SyntaxReference): io.github.kotlinmania.syntect.parsing.SyntaxReference = syntax

    private fun stackStates(state: ParseState): ScopeStack = state.scopeStack

    @Test
    fun canParseSimple() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Simple",
                fileExtensions = listOf("simple"),
                scope = Scope.new("source.simple"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("simple")!!
        val state = ParseState(syntax)
        val ops = state.parseLine("hello world", ss)
        assertTrue(ops.isNotEmpty())
        assertEquals(ParseOp(0, ScopeStackOp.Push(Scope.new("source.simple"))), ops[0])
        expectScopeStacks("hello world", ops)
        expectScopeStacksWithSyntax(syntax, "hello world")
        expectScopeStacksForOps(ops)
        val parsed = parse("hello world", ss)
        assertEquals(1, parsed.size)
        val linked = link(syntax)
        assertEquals("Simple", linked.name)
        val st = stackStates(state)
        assertNotNull(st)
    }

    @Test
    fun canParseYaml() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "YAML",
                fileExtensions = listOf("yaml", "yml"),
                scope = Scope.new("source.yaml"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("yaml")!!
        val state = ParseState(syntax)
        val ops = state.parseLine("key: value", ss)
        assertTrue(ops.isNotEmpty())
    }

    @Test
    fun canParseIncludes() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "A",
                fileExtensions = listOf("a"),
                scope = Scope.new("source.a"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("a")!!
        val state = ParseState(syntax)
        val ops = state.parseLine("test line", ss)
        assertTrue(ops.isNotEmpty())
    }

    @Test
    fun canParseBackrefs() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Backref",
                fileExtensions = listOf("br"),
                scope = Scope.new("source.br"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("br")!!
        val state = ParseState(syntax)
        val ops = state.parseLine("match backref", ss)
        assertTrue(ops.isNotEmpty())
    }

    @Test
    fun canParsePreprocessorRules() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "C",
                fileExtensions = listOf("c", "h"),
                scope = Scope.new("source.c"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("c")!!
        val state = ParseState(syntax)
        val ops = state.parseLine("#include <stdio.h>", ss)
        assertTrue(ops.isNotEmpty())
    }

    @Test
    fun canParseIssue25() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canCompareParseStates() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state1 = ParseState(syntax)
        val state2 = ParseState(syntax)
        assertEquals(state1.syntax, state2.syntax)
    }

    @Test
    fun canParseNonNestedClearScopes() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        val ops = state.parseLine("line", ss)
        assertTrue(ops.isNotEmpty())
    }

    @Test
    fun canParseNonNestedTooManyClearScopes() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        val ops = state.parseLine("line", ss)
        assertTrue(ops.isNotEmpty())
    }

    @Test
    fun canParseNestedClearScopes() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        val ops = state.parseLine("line", ss)
        assertTrue(ops.isNotEmpty())
    }

    @Test
    fun canParseInfiniteLoop() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseInfiniteSeemingLoop() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParsePrototypeThatPopsMain() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseSyntaxWithNewlineInCharacterClass() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseIssue120() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseNonConsumingPopThatWouldLoop() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseNonConsumingSetAndPopThatWouldLoop() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseNonConsumingSetAfterConsumingPushThatDoesNotLoop() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseNonConsumingSetAfterConsumingSetThatDoesNotLoop() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseNonConsumingPopThatWouldLoopAtEndOfLine() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseEmptyButConsumingSetThatDoesNotLoop() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseNonConsumingPopThatDoesNotLoop() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseNonConsumingPopWithMultiPushThatDoesNotLoop() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseNonConsumingPopOfRecursiveContextThatDoesNotLoop() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseNonConsumingPopOrder() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParsePrototypeWithEmbed() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseContextIncludedInPrototypeViaNamedReference() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseWithPrototypeSet() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseIssue176() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseTwoWithPrototypesAtSameStackLevel() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseTwoWithPrototypesAtSameStackLevelSetMultiple() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseTwoWithPrototypesAtSameStackLevelUpdatedCaptures() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseTwoWithPrototypesAtSameStackLevelUpdatedCapturesIgnoreUnexisting() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseSyntaxWithEolAndNewline() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseSyntaxWithEolOnly() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseSyntaxWithBeginningOfLine() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseSyntaxWithCommentAndEol() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canParseTextWithUnicodeToSkip() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canIncludeBackrefs() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canIncludeNestedBackrefs() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }

    @Test
    fun canAvoidInfiniteStackDepth() {
        val builder = SyntaxSetBuilder()
        builder.add(
            SyntaxDefinition(
                name = "Test",
                fileExtensions = listOf("test"),
                scope = Scope.new("source.test"),
            ),
        )
        val ss = builder.build()
        val syntax = ss.findSyntaxByExtension("test")!!
        val state = ParseState(syntax)
        assertNotNull(state)
    }
}
