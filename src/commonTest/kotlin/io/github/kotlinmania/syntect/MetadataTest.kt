// port-lint: tests parsing/metadata.rs
package io.github.kotlinmania.syntect

import io.github.kotlinmania.syntect.highlighting.ScopeSelectors
import io.github.kotlinmania.syntect.parsing.BlockComment
import io.github.kotlinmania.syntect.parsing.Metadata
import io.github.kotlinmania.syntect.parsing.MetadataItems
import io.github.kotlinmania.syntect.parsing.MetadataSet
import io.github.kotlinmania.syntect.parsing.Regex
import io.github.kotlinmania.syntect.parsing.Scope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetadataTest {
    @Test
    fun loadRaw() {
        val meta =
            MetadataSet(
                selectorString = "source.rust",
                selector = ScopeSelectors.fromString("source.rust"),
                items =
                    MetadataItems(
                        lineComment = "//",
                        blockComment = BlockComment("/*", "*/"),
                    ),
            )
        assertEquals("source.rust", meta.selectorString)
        assertEquals("//", meta.items.lineComment)
    }

    @Test
    fun loadGroups() {
        val meta =
            Metadata(
                scopedMetadata =
                    listOf(
                        MetadataSet(
                            selectorString = "source.rust",
                            selector = ScopeSelectors.fromString("source.rust"),
                            items =
                                MetadataItems(
                                    increaseIndentPattern = Regex.new("^.*\\{[^}]*$"),
                                    lineComment = "//",
                                ),
                        ),
                    ),
            )
        assertEquals(1, meta.scopedMetadata.size)
        val rustMeta = meta.scopedMetadata.first()
        assertEquals("source.rust", rustMeta.selectorString)
        assertNotNull(rustMeta.items.increaseIndentPattern)
        assertNotNull(rustMeta.items.lineComment)
    }

    @Test
    fun parseYamlMeta() {
        val items =
            MetadataItems(
                increaseIndentPattern = Regex.new("^.*\\{[^}]*$"),
                decreaseIndentPattern = Regex.new("^.*\\}[^{]*$"),
            )
        assertNotNull(items.increaseIndentPattern)
        assertNotNull(items.decreaseIndentPattern)
    }

    @Test
    fun loadShellVars() {
        val items =
            MetadataItems(
                shellVariables = mapOf("TM_COMMENT_START" to "//"),
                lineComment = "//",
            )
        assertTrue(items.shellVariables.containsKey("TM_COMMENT_START"))
        assertEquals("//", items.shellVariables["TM_COMMENT_START"])
    }

    @Test
    fun indentRust() {
        val meta =
            Metadata(
                scopedMetadata =
                    listOf(
                        MetadataSet(
                            selectorString = "source.rust",
                            selector = ScopeSelectors.fromString("source.rust"),
                            items =
                                MetadataItems(
                                    increaseIndentPattern = Regex.new("^.*\\{[^}]*$"),
                                    decreaseIndentPattern = Regex.new("^.*\\}[^{]*$"),
                                ),
                        ),
                    ),
            )
        val scopes = listOf(Scope.new("source.rust"))
        val ctx = meta.metadataForScope(scopes)
        assertEquals(1, ctx.items.size)
    }
}
