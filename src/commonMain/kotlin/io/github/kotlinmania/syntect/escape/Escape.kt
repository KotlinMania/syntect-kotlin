// port-lint: source escape.rs
package io.github.kotlinmania.syntect.escape

/**
 * Wrapper struct which will emit the HTML-escaped version of the contained
 * string when passed to a format string.
 */
class Escape(
    val s: String,
) {
    override fun toString(): String = escapeHtml(s)
}

/**
 * Escapes characters for HTML rendering: `<` (`&lt;`), `>` (`&gt;`), `&` (`&amp;`), `'` (`&#39;`), `"` (`&quot;`).
 */
fun escapeHtml(s: String): String {
    val sb = StringBuilder(s.length)
    for (ch in s) {
        when (ch) {
            '>' -> sb.append("&gt;")
            '<' -> sb.append("&lt;")
            '&' -> sb.append("&amp;")
            '\'' -> sb.append("&#39;")
            '"' -> sb.append("&quot;")
            else -> sb.append(ch)
        }
    }
    return sb.toString()
}
