// port-lint: source syntect/src/parsing/syntax_definition.rs
package io.github.kotlinmania.syntect.parsing

/**
 * Identifier for a context within a syntax set.
 */
data class ContextId(
    val syntaxIndex: Int,
    val contextIndex: Int,
)

/**
 * A syntax definition referencing syntax rules and scope bindings.
 */
data class SyntaxDefinition(
    val name: String,
    val fileExtensions: List<String> = emptyList(),
    val scope: Scope = Scope(),
    val firstLineMatch: String? = null,
    val hidden: Boolean = false,
    val variables: Map<String, String> = emptyMap(),
)

/**
 * A linked version of a SyntaxDefinition that is part of a SyntaxSet.
 */
data class SyntaxReference(
    val name: String,
    val fileExtensions: List<String> = emptyList(),
    val scope: Scope = Scope(),
    val firstLineMatch: String? = null,
    val hidden: Boolean = false,
    val variables: Map<String, String> = emptyMap(),
)
