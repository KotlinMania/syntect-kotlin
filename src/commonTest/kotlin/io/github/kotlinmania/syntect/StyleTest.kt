// port-lint: tests syntect/src/highlighting/style.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.highlighting.Color
import io.github.kotlinmania.syntect.highlighting.FontStyle
import io.github.kotlinmania.syntect.highlighting.Style
import io.github.kotlinmania.syntect.highlighting.StyleModifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StyleTest {
    @Test
    fun testColorParsing() {
        val c3 = Color.fromHex("#F00")
        assertEquals(Color(255u, 0u, 0u, 255u), c3)

        val c6 = Color.fromHex("#D08770")
        assertEquals(Color(208u, 135u, 112u, 255u), c6)

        val c8 = Color.fromHex("#2B303BFF")
        assertEquals(Color(43u, 48u, 59u, 255u), c8)

        assertFailsWith<ThemeException> {
            Color.fromHex("invalid")
        }
        assertFailsWith<ThemeException> {
            Color.fromHex("#12")
        }
    }

    @Test
    fun testFontStyleBitflags() {
        val empty = FontStyle.empty()
        assertTrue(empty.isEmpty())
        assertFalse(empty.isAll())

        val bold = FontStyle.BOLD
        val italic = FontStyle.ITALIC
        val underline = FontStyle.UNDERLINE

        assertTrue(bold.contains(FontStyle.BOLD))
        assertFalse(bold.contains(FontStyle.ITALIC))

        val combined = bold.union(italic)
        assertTrue(combined.contains(FontStyle.BOLD))
        assertTrue(combined.contains(FontStyle.ITALIC))
        assertFalse(combined.contains(FontStyle.UNDERLINE))

        val diff = combined.difference(bold)
        assertEquals(FontStyle.ITALIC, diff)

        val parsed = FontStyle.fromString("bold italic")
        assertEquals(combined, parsed)
    }

    @Test
    fun testStyleApply() {
        val defaultStyle = Style.default()
        assertEquals(Color.BLACK, defaultStyle.foreground)
        assertEquals(Color.WHITE, defaultStyle.background)
        assertEquals(FontStyle.empty(), defaultStyle.fontStyle)

        val modifier =
            StyleModifier(
                foreground = Color(200u, 100u, 50u, 255u),
                fontStyle = FontStyle.BOLD,
            )
        val modified = defaultStyle.apply(modifier)
        assertEquals(Color(200u, 100u, 50u, 255u), modified.foreground)
        assertEquals(Color.WHITE, modified.background)
        assertEquals(FontStyle.BOLD, modified.fontStyle)

        val secondMod =
            StyleModifier(
                background = Color(10u, 20u, 30u, 255u),
            )
        val combinedMod = modifier.apply(secondMod)
        assertEquals(Color(200u, 100u, 50u, 255u), combinedMod.foreground)
        assertEquals(Color(10u, 20u, 30u, 255u), combinedMod.background)
        assertEquals(FontStyle.BOLD, combinedMod.fontStyle)
    }
}
