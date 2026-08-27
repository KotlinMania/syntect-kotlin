// port-lint: source syntect/src/parsing/metadata.rs
package io.github.kotlinmania.syntect.parsing

import io.github.kotlinmania.syntect.highlighting.ScopeSelectors

/**
 * Metadata for a particular scope selector.
 */
data class MetadataSet(
    val selectorString: String,
    val selector: ScopeSelectors,
    val items: MetadataItems,
)

/**
 * Block comment start and end markers.
 */
data class BlockComment(
    val start: String,
    val end: String,
)

/**
 * Items loaded from metadata files for a particular scope.
 */
data class MetadataItems(
    val increaseIndentPattern: Regex? = null,
    val decreaseIndentPattern: Regex? = null,
    val bracketIndentNextLinePattern: Regex? = null,
    val disableIndentNextLinePattern: Regex? = null,
    val unindentedLinePattern: Regex? = null,
    val indentParens: Boolean? = null,
    val shellVariables: Map<String, String> = emptyMap(),
    val lineComment: String? = null,
    val blockComment: BlockComment? = null,
)

/**
 * Scored metadata match.
 */
data class ScopedMetadataMatch(
    val score: MatchPower,
    val metadataSet: MetadataSet,
)

/**
 * A collection of all loaded metadata.
 */
data class Metadata(
    val scopedMetadata: List<MetadataSet> = emptyList(),
) {
    fun metadataForScope(scope: List<Scope>): ScopedMetadata {
        val matches =
            scopedMetadata
                .mapNotNull { metaSet ->
                    metaSet.selector.doesMatch(scope)?.let { score -> ScopedMetadataMatch(score, metaSet) }
                }.sortedByDescending { it.score }
        return ScopedMetadata(matches)
    }
}

/**
 * Scoped metadata matching a scope stack.
 */
class ScopedMetadata(
    val items: List<ScopedMetadataMatch>,
) {
    fun isNotEmpty(): Boolean = items.isNotEmpty()

    fun isEmpty(): Boolean = items.isEmpty()
}
