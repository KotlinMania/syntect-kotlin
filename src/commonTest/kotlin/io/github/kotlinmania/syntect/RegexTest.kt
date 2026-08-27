// port-lint: tests syntect/src/parsing/regex.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.parsing.Regex
import io.github.kotlinmania.syntect.parsing.Region
import io.github.kotlinmania.syntect.parsing.RegionPosition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RegexTest {
    @Test
    fun cachesCompiledRegex() {
        val regex = Regex.new("\\w+")
        assertFalse(regex.isCompiled())
        assertTrue(regex.isMatch("test"))
        assertTrue(regex.isCompiled())
    }

    @Test
    fun serdeAsString() {
        val pattern = Regex.new("just a string")
        assertEquals("just a string", pattern.regexStr)
        assertEquals("just a string", pattern.toString())
    }

    @Test
    fun testRegexSearchWithRegion() {
        val regex = Regex.new("(\\w+)=(\\d+)")
        val text = "foo=123"
        val region = Region.new()
        val found = regex.search(text, 0, text.length, region)
        assertTrue(found)
        assertEquals(RegionPosition(0, 7), region.pos(0))
        assertEquals(RegionPosition(0, 3), region.pos(1))
        assertEquals(RegionPosition(4, 7), region.pos(2))
    }

    @Test
    fun testTryCompile() {
        assertNull(Regex.tryCompile("[a-z]+"))
    }
}
