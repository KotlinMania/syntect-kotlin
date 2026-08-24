// port-lint: source highlighting/style.rs
package io.github.kotlinmania.syntect.highlighting

import io.github.kotlinmania.syntect.ThemeException

/**
 * RGBA color, directly from the theme.
 */
data class Color(
    val r: UByte,
    val g: UByte,
    val b: UByte,
    val a: UByte = 0xFFu,
) {
    override fun toString(): String = "Color(r=$r, g=$g, b=$b, a=$a)"

    companion object {
        val BLACK = Color(0x00u, 0x00u, 0x00u, 0xFFu)
        val WHITE = Color(0xFFu, 0xFFu, 0xFFu, 0xFFu)

        /**
         * Parses a hex color string like `#RGB`, `#RRGGBB`, or `#RRGGBBAA`.
         */
        fun fromHex(s: String): Color {
            if (!s.startsWith("#")) {
                throw ThemeException("Color string must start with '#': $s")
            }
            val hex = s.substring(1)
            val digits =
                hex.map {
                    it.digitToIntOrNull(16)
                        ?: throw ThemeException("Invalid hex digit in color: $it")
                }
            return when (digits.size) {
                3 -> {
                    Color(
                        r = (digits[0] * 17).toUByte(),
                        g = (digits[1] * 17).toUByte(),
                        b = (digits[2] * 17).toUByte(),
                        a = 0xFFu,
                    )
                }
                6 -> {
                    Color(
                        r = (digits[0] * 16 + digits[1]).toUByte(),
                        g = (digits[2] * 16 + digits[3]).toUByte(),
                        b = (digits[4] * 16 + digits[5]).toUByte(),
                        a = 0xFFu,
                    )
                }
                8 -> {
                    Color(
                        r = (digits[0] * 16 + digits[1]).toUByte(),
                        g = (digits[2] * 16 + digits[3]).toUByte(),
                        b = (digits[4] * 16 + digits[5]).toUByte(),
                        a = (digits[6] * 16 + digits[7]).toUByte(),
                    )
                }
                else -> throw ThemeException("Invalid color hex length: $s")
            }
        }
    }
}

/**
 * Color-independent styling of a font (bold, italic, underline).
 */
data class FontStyle(
    val bits: UByte = 0u,
) {
    fun contains(other: FontStyle): Boolean {
        val otherBits = other.bits.toInt()
        return (this.bits.toInt() and otherBits) == otherBits
    }

    fun union(other: FontStyle): FontStyle = FontStyle((this.bits.toInt() or other.bits.toInt()).toUByte())

    fun intersection(other: FontStyle): FontStyle = FontStyle((this.bits.toInt() and other.bits.toInt()).toUByte())

    fun difference(other: FontStyle): FontStyle = FontStyle((this.bits.toInt() and other.bits.toInt().inv()).toUByte())

    fun symmetricDifference(other: FontStyle): FontStyle = FontStyle((this.bits.toInt() xor other.bits.toInt()).toUByte())

    fun complement(): FontStyle = FontStyle((this.bits.toInt().inv() and all().bits.toInt()).toUByte())

    fun isEmpty(): Boolean = this.bits == 0u.toUByte()

    fun isAll(): Boolean = this.bits == all().bits

    companion object {
        val BOLD = FontStyle(1u)
        val UNDERLINE = FontStyle(2u)
        val ITALIC = FontStyle(4u)

        fun empty(): FontStyle = FontStyle(0u)

        fun all(): FontStyle = FontStyle(7u)

        fun fromString(s: String): FontStyle {
            var result = empty()
            for (token in s.split(Regex("\\s+")).filter { it.isNotEmpty() }) {
                when (token.lowercase()) {
                    "bold" -> result = result.union(BOLD)
                    "underline" -> result = result.union(UNDERLINE)
                    "italic" -> result = result.union(ITALIC)
                    "normal", "regular" -> Unit
                    else -> throw ThemeException("Incorrect font style: $token")
                }
            }
            return result
        }
    }
}

/**
 * Foreground and background colors, with font style.
 */
data class Style(
    val foreground: Color = Color.BLACK,
    val background: Color = Color.WHITE,
    val fontStyle: FontStyle = FontStyle.empty(),
) {
    /**
     * Applies a change to this style, yielding a new style.
     */
    fun apply(modifier: StyleModifier): Style =
        Style(
            foreground = modifier.foreground ?: this.foreground,
            background = modifier.background ?: this.background,
            fontStyle = modifier.fontStyle ?: this.fontStyle,
        )

    companion object {
        fun default(): Style =
            Style(
                foreground = Color.BLACK,
                background = Color.WHITE,
                fontStyle = FontStyle.empty(),
            )
    }
}

/**
 * A change to a Style applied incrementally by a theme rule.
 */
data class StyleModifier(
    val foreground: Color? = null,
    val background: Color? = null,
    val fontStyle: FontStyle? = null,
) {
    /**
     * Applies another modifier on top of this one, preferring the other's values.
     */
    fun apply(other: StyleModifier): StyleModifier =
        StyleModifier(
            foreground = other.foreground ?: this.foreground,
            background = other.background ?: this.background,
            fontStyle = other.fontStyle ?: this.fontStyle,
        )
}

/**
 * A styled slice of text produced by a highlighter.
 */
data class HighlightedSegment(
    val style: Style,
    val text: String,
)
