// port-lint: source syntect/src/highlighting/theme.rs
package io.github.kotlinmania.syntect.highlighting

import io.github.kotlinmania.syntect.ThemeException

/**
 * Underline display option.
 */
enum class UnderlineOption {
    None,
    Underline,
    StippledUnderline,
    SquigglyUnderline,
    ;

    companion object {
        fun fromString(s: String): UnderlineOption =
            when (s.lowercase()) {
                "underline" -> Underline
                "stippled_underline" -> StippledUnderline
                "squiggly_underline" -> SquigglyUnderline
                "none" -> None
                else -> throw ThemeException("Incorrect underline option: $s")
            }
    }
}

/**
 * Properties for styling the UI of a text editor.
 */
data class ThemeSettings(
    val foreground: Color? = null,
    val background: Color? = null,
    val caret: Color? = null,
    val lineHighlight: Color? = null,
    val misspelling: Color? = null,
    val minimapBorder: Color? = null,
    val accent: Color? = null,
    val popupCss: String? = null,
    val phantomCss: String? = null,
    val bracketContentsForeground: Color? = null,
    val bracketContentsOptions: UnderlineOption? = null,
    val bracketsForeground: Color? = null,
    val bracketsBackground: Color? = null,
    val bracketsOptions: UnderlineOption? = null,
    val tagsForeground: Color? = null,
    val tagsOptions: UnderlineOption? = null,
    val highlight: Color? = null,
    val findHighlight: Color? = null,
    val findHighlightForeground: Color? = null,
    val gutter: Color? = null,
    val gutterForeground: Color? = null,
    val selection: Color? = null,
    val selectionForeground: Color? = null,
    val selectionBorder: Color? = null,
    val inactiveSelection: Color? = null,
    val inactiveSelectionForeground: Color? = null,
    val guide: Color? = null,
    val activeGuide: Color? = null,
    val stackGuide: Color? = null,
    val shadow: Color? = null,
)

/**
 * A component of a theme meant to highlight a specific scope in a certain way.
 */
data class ThemeItem(
    val scope: ScopeSelectors = ScopeSelectors(),
    val style: StyleModifier = StyleModifier(),
)

/**
 * A theme parsed from a theme definition.
 */
data class Theme(
    val name: String? = null,
    val author: String? = null,
    val settings: ThemeSettings = ThemeSettings(),
    val scopes: List<ThemeItem> = emptyList(),
)
