// port-lint: tests highlighting/theme_set.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.highlighting.Color
import io.github.kotlinmania.syntect.highlighting.Theme
import io.github.kotlinmania.syntect.highlighting.ThemeSet
import io.github.kotlinmania.syntect.highlighting.ThemeSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ThemeSetTest {
    @Test
    fun canParseCommonThemes() {
        val themeSet = ThemeSet.new()
        val theme =
            Theme(
                name = "Base16 Ocean Dark",
                settings =
                    ThemeSettings(
                        selection = Color(0x4fu, 0x5bu, 0x66u, 0xffu),
                        gutterForeground = Color(0x65u, 0x73u, 0x7eu, 0xffu),
                    ),
            )
        themeSet.addTheme("base16-ocean.dark", theme)

        assertTrue(themeSet.themes.containsKey("base16-ocean.dark"))
        val retrieved = themeSet.getTheme("base16-ocean.dark")
        assertNotNull(retrieved)
        assertEquals("Base16 Ocean Dark", retrieved.name)
        assertEquals(Color(0x4fu, 0x5bu, 0x66u, 0xffu), retrieved.settings.selection)
        assertEquals(Color(0x65u, 0x73u, 0x7eu, 0xffu), retrieved.settings.gutterForeground)
    }
}
