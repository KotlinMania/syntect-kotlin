// port-lint: tests syntect/src/highlighting/selector.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.highlighting.ScopeSelector
import io.github.kotlinmania.syntect.highlighting.ScopeSelectors
import io.github.kotlinmania.syntect.parsing.MatchPower
import io.github.kotlinmania.syntect.parsing.ScopeStack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SelectorTest {
    @Test
    fun selectorsWork() {
        val sels =
            ScopeSelectors.fromString(
                "source.php meta.preprocessor - string.quoted, source string",
            )
        assertEquals(2, sels.selectors.size)
        val firstSel = sels.selectors[0]
        assertEquals("source.php meta.preprocessor", firstSel.path.toString())
        assertEquals(1, firstSel.excludes.size)
        assertEquals("string.quoted", firstSel.excludes[0].toString())

        val pipeSels = ScopeSelectors.fromString(" -a.b|j.g")
        assertEquals(2, pipeSels.selectors.size)
        assertEquals("", pipeSels.selectors[0].path.toString())
        assertEquals(1, pipeSels.selectors[0].excludes.size)
        assertEquals("a.b", pipeSels.selectors[0].excludes[0].toString())
        assertEquals("j.g", pipeSels.selectors[1].path.toString())
    }

    @Test
    fun matchingWorks() {
        // 0o20 in octal = 16
        assertEquals(
            MatchPower(16.0),
            ScopeSelectors
                .fromString("a.b, a e, e.f")
                .doesMatch(ScopeStack.fromString("a.b e.f").asSlice()),
        )
        // 0o21 in octal = 17
        assertEquals(
            MatchPower(17.0),
            ScopeSelectors
                .fromString("a.b, a e.f, e.f")
                .doesMatch(ScopeStack.fromString("a.b e.f").asSlice()),
        )
        // 0o2000 in octal = 2 * 512 = 1024
        assertEquals(
            MatchPower(1024.0),
            ScopeSelectors
                .fromString("a.b, a e.f - c j, e.f")
                .doesMatch(ScopeStack.fromString("a.b c.d j e.f").asSlice()),
        )
        // 0o2 in octal = 2
        assertEquals(
            MatchPower(2.0),
            ScopeSelectors
                .fromString("a.b, a e.f - c j, e.f - a.b")
                .doesMatch(ScopeStack.fromString("a.b c.d j e.f").asSlice()),
        )
        // 0o2001 in octal = 2 * 512 + 1 = 1025
        assertEquals(
            MatchPower(1025.0),
            ScopeSelectors
                .fromString("a.b, a e.f - c k, e.f - a.b")
                .doesMatch(ScopeStack.fromString("a.b c.d j e.f").asSlice()),
        )
        // 0o201 in octal = 2 * 64 + 1 = 129
        assertEquals(
            MatchPower(129.0),
            ScopeSelectors
                .fromString("a.b|a e.f -d, e.f -a.b")
                .doesMatch(ScopeStack.fromString("a.b c.d e.f").asSlice()),
        )
    }

    @Test
    fun emptyStackMatchingWorks() {
        assertNull(
            ScopeSelector
                .fromString(" - a.b")
                .doesMatch(ScopeStack.fromString("a.b c.d j e.f").asSlice()),
        )
        assertEquals(
            MatchPower(1.0),
            ScopeSelector
                .fromString("")
                .doesMatch(ScopeStack.fromString("a.b c.d j e.f").asSlice()),
        )
        assertEquals(
            MatchPower(1.0),
            ScopeSelector
                .fromString("")
                .doesMatch(ScopeStack.fromString("").asSlice()),
        )
    }

    @Test
    fun multipleExcludesMatchingWorks() {
        assertNull(
            ScopeSelector
                .fromString(" - a.b - c.d")
                .doesMatch(ScopeStack.fromString("a.b c.d j e.f").asSlice()),
        )
        assertEquals(
            MatchPower(1.0),
            ScopeSelector
                .fromString(" - a.b - c.d")
                .doesMatch(ScopeStack.fromString("").asSlice()),
        )
        assertNull(
            ScopeSelector
                .fromString("a.b - c.d -e.f")
                .doesMatch(ScopeStack.fromString("").asSlice()),
        )
        assertEquals(
            MatchPower(2.0),
            ScopeSelector
                .fromString("a.b")
                .doesMatch(ScopeStack.fromString("a.b c.d j e.f").asSlice()),
        )
        assertEquals(
            MatchPower(2.0),
            ScopeSelector
                .fromString("a.b -g.h - h.i")
                .doesMatch(ScopeStack.fromString("a.b c.d j e.f").asSlice()),
        )
    }
}
