// port-lint: source syntect/src/highlighting/selector.rs
package io.github.kotlinmania.syntect.highlighting

import io.github.kotlinmania.syntect.parsing.MatchPower
import io.github.kotlinmania.syntect.parsing.Scope
import io.github.kotlinmania.syntect.parsing.ScopeStack

/**
 * A single selector consisting of a stack to match and possible stacks to exclude.
 */
data class ScopeSelector(
    val path: ScopeStack,
    val excludes: List<ScopeStack> = emptyList(),
) {
    /**
     * Checks if this selector matches a given scope stack.
     */
    fun doesMatch(stack: List<Scope>): MatchPower? {
        if (excludes.any { it.isEmpty() || it.doesMatch(stack) != null }) {
            return null
        }
        return if (path.isEmpty()) {
            MatchPower(1.0)
        } else {
            path.doesMatch(stack)
        }
    }

    /**
     * If this selector is really just a single scope, returns it.
     */
    fun extractSingleScope(): Scope? {
        if (path.len() != 1 || excludes.isNotEmpty() || path.isEmpty()) {
            return null
        }
        return path.asSlice()[0]
    }

    /**
     * Extracts all scopes from the path.
     */
    fun extractScopes(): List<Scope> = path.scopes.toList()

    companion object {
        fun fromString(s: String): ScopeSelector {
            val excludes = mutableListOf<ScopeStack>()
            var pathStr = ""
            val parts = s.split(" -")
            for ((i, selector) in parts.withIndex()) {
                if (i == 0) {
                    pathStr = selector
                } else {
                    excludes.add(ScopeStack.fromString(selector))
                }
            }
            return ScopeSelector(
                path = ScopeStack.fromString(pathStr),
                excludes = excludes,
            )
        }
    }
}

/**
 * A selector set that matches anything matched by any of its component selectors.
 */
data class ScopeSelectors(
    val selectors: List<ScopeSelector> = emptyList(),
) {
    /**
     * Checks if any of the given selectors match the given scope stack, returning the maximum score.
     */
    fun doesMatch(stack: List<Scope>): MatchPower? = selectors.mapNotNull { it.doesMatch(stack) }.maxOrNull()

    companion object {
        fun fromString(s: String): ScopeSelectors {
            val selectors = mutableListOf<ScopeSelector>()
            val splitPattern = Regex("[,|]")
            for (selector in s.split(splitPattern)) {
                selectors.add(ScopeSelector.fromString(selector))
            }
            return ScopeSelectors(selectors)
        }
    }
}
