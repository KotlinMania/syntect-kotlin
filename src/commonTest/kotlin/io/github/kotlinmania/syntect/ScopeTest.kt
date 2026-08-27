// port-lint: tests parsing/scope.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.parsing.BasicScopeStackOp
import io.github.kotlinmania.syntect.parsing.ClearAmount
import io.github.kotlinmania.syntect.parsing.MatchPower
import io.github.kotlinmania.syntect.parsing.Scope
import io.github.kotlinmania.syntect.parsing.ScopeRepository
import io.github.kotlinmania.syntect.parsing.ScopeStack
import io.github.kotlinmania.syntect.parsing.ScopeStackOp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ScopeTest {
    @Test
    fun misc() {
        assertEquals(Scope.new("source.php"), Scope.new("source.php"))
    }

    @Test
    fun repoWorks() {
        val repo = ScopeRepository()
        assertEquals(repo.build("source.php"), repo.build("source.php"))
        assertEquals(
            repo.build("source.php.wow.hi.bob.troll.clock.5"),
            repo.build("source.php.wow.hi.bob.troll.clock.5"),
        )
        assertEquals(repo.build(""), repo.build(""))

        val s1 = repo.build("")
        assertEquals("", repo.toString(s1))

        val s2 = repo.build("source.php.wow")
        assertEquals("source.php.wow", repo.toString(s2))

        assertNotEquals(repo.build("source.php"), repo.build("source.perl"))
        assertNotEquals(repo.build("source.php"), repo.build("source.php.wagon"))
        assertEquals(repo.build("comment.line."), repo.build("comment.line"))
    }

    @Test
    fun globalRepoWorks() {
        assertEquals(Scope.new("source.php"), Scope.new("source.php"))
        assertEquals(Scope.fromString("1.2.3.4.5.6.7.8").len(), 8)
        assertFailsWith<ScopeException> {
            Scope.fromString("1.2.3.4.5.6.7.8.9")
        }
    }

    @Test
    fun prefixesWork() {
        assertTrue(Scope.new("1.2.3.4.5.6.7.8").isPrefixOf(Scope.new("1.2.3.4.5.6.7.8")))
        assertTrue(Scope.new("1.2.3.4.5.6").isPrefixOf(Scope.new("1.2.3.4.5.6.7.8")))
        assertTrue(Scope.new("1.2.3.4").isPrefixOf(Scope.new("1.2.3.4.5.6.7.8")))
        assertFalse(Scope.new("1.2.3.4.5.6.a").isPrefixOf(Scope.new("1.2.3.4.5.6.7.8")))
        assertFalse(Scope.new("1.2.a.4.5.6.7").isPrefixOf(Scope.new("1.2.3.4.5.6.7.8")))
        assertFalse(Scope.new("1.2.a.4.5.6.7").isPrefixOf(Scope.new("1.2.3.4.5")))
        assertFalse(Scope.new("1.2.a").isPrefixOf(Scope.new("1.2.3.4.5.6.7.8")))
    }

    @Test
    fun matchingWorks() {
        assertEquals(
            MatchPower(1.0),
            ScopeStack
                .fromString("string")
                .doesMatch(ScopeStack.fromString("string.quoted").asSlice()),
        )
        assertEquals(
            null,
            ScopeStack
                .fromString("source")
                .doesMatch(ScopeStack.fromString("string.quoted").asSlice()),
        )
        assertEquals(
            MatchPower(130.0), // 0o202 in octal = 2 * 64 + 2 = 130
            ScopeStack
                .fromString("a.b e.f")
                .doesMatch(ScopeStack.fromString("a.b c.d e.f.g").asSlice()),
        )
        assertEquals(
            MatchPower(136.0), // 0o210 in octal = 2 * 64 + 8 = 136
            ScopeStack
                .fromString("c e.f")
                .doesMatch(ScopeStack.fromString("a.b c.d e.f.g").asSlice()),
        )
        assertEquals(
            MatchPower(144.0), // 0o220 in octal = 2 * 64 + 2 * 8 = 144
            ScopeStack
                .fromString("c.d e.f")
                .doesMatch(ScopeStack.fromString("a.b c.d e.f.g").asSlice()),
        )
        assertEquals(
            MatchPower(138.0), // 0o212 in octal = 2 * 64 + 8 + 2 = 138
            ScopeStack
                .fromString("a.b c e.f")
                .doesMatch(ScopeStack.fromString("a.b c.d e.f.g").asSlice()),
        )
        assertEquals(
            MatchPower(17.0), // 0o021 in octal = 2 * 8 + 1 = 17
            ScopeStack
                .fromString("a c.d")
                .doesMatch(ScopeStack.fromString("a.b c.d e.f.g").asSlice()),
        )
        assertEquals(
            null,
            ScopeStack
                .fromString("a c.d.e")
                .doesMatch(ScopeStack.fromString("a.b c.d e.f.g").asSlice()),
        )
    }

    @Test
    fun testScopeStackOperations() {
        val stack = ScopeStack()
        val s1 = Scope.new("source.kotlin")
        val s2 = Scope.new("keyword.declaration")
        val s3 = Scope.new("string.quoted")

        stack.push(s1)
        stack.push(s2)
        assertEquals(2, stack.len())

        val ops = mutableListOf<String>()
        stack.applyWithHook(ScopeStackOp.Push(s3)) { op, cur ->
            when (op) {
                is BasicScopeStackOp.Push -> ops.add("push ${cur.size}")
                is BasicScopeStackOp.Pop -> ops.add("pop ${cur.size}")
            }
        }
        assertEquals(listOf("push 3"), ops)
        assertEquals(3, stack.len())

        stack.apply(ScopeStackOp.Clear(ClearAmount.TopN(2)))
        assertEquals(1, stack.len())
        assertEquals(s1, stack.asSlice()[0])

        stack.apply(ScopeStackOp.Restore)
        assertEquals(3, stack.len())
    }
}
