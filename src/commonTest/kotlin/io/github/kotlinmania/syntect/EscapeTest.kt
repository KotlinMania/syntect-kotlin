// port-lint: tests syntect/src/escape.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.escape.Escape
import io.github.kotlinmania.syntect.escape.escapeHtml
import kotlin.test.Test
import kotlin.test.assertEquals

class EscapeTest {
    @Test
    fun testEscapeHtml() {
        assertEquals("&lt;div&gt;", escapeHtml("<div>"))
        assertEquals("&amp;&quot;&#39;", escapeHtml("&\"'"))
        assertEquals("Hello, World!", escapeHtml("Hello, World!"))
        assertEquals("a &lt; b &amp;&amp; c &gt; d", Escape("a < b && c > d").toString())
    }
}
