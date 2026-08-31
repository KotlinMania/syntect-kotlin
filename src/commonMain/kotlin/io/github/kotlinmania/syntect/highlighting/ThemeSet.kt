// port-lint: source syntect/src/highlighting/theme_set.rs
package io.github.kotlinmania.syntect.highlighting

/**
 * A set of themes with lookup by name.
 */
class ThemeSet(
    themes: Map<String, Theme> = emptyMap(),
) {
    private val _themes = HashMap(themes)
    val themes: Map<String, Theme> get() = _themes

    fun addTheme(
        name: String,
        theme: Theme,
    ) {
        _themes[name] = theme
    }

    fun getTheme(name: String): Theme? = _themes[name]

    companion object {
        fun new(): ThemeSet = ThemeSet()
    }
}
