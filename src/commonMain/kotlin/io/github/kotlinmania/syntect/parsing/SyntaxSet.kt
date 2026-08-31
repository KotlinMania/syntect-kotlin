// port-lint: source syntect/src/parsing/syntax_set.rs
package io.github.kotlinmania.syntect.parsing
import kotlin.text.Regex

/**
 * A mapping from a file path to a syntax index within a SyntaxSet.
 */
data class PathSyntax(
    val path: String,
    val syntaxIndex: Int,
)

/**
 * A set of syntax definitions linked together for parsing and highlighting.
 */
class SyntaxSet(
    val syntaxes: List<SyntaxReference> = emptyList(),
    val pathSyntaxes: List<PathSyntax> = emptyList(),
) {
    /**
     * Finds a syntax by its default scope.
     */
    fun findSyntaxByScope(scope: Scope): SyntaxReference? = syntaxes.asReversed().find { it.scope == scope }

    /**
     * Finds a syntax by its exact name.
     */
    fun findSyntaxByName(name: String): SyntaxReference? = syntaxes.asReversed().find { it.name == name }

    /**
     * Finds a syntax by one of its file extensions (case-insensitive).
     */
    fun findSyntaxByExtension(extension: String): SyntaxReference? =
        syntaxes.asReversed().find { s ->
            s.fileExtensions.any { it.equals(extension, ignoreCase = true) }
        }

    /**
     * Searches for a syntax first by extension and then by case-insensitive name.
     */
    fun findSyntaxByToken(s: String): SyntaxReference? =
        findSyntaxByExtension(s)
            ?: syntaxes.asReversed().find { it.name.equals(s, ignoreCase = true) }

    /**
     * Finds a syntax by matching a file's first line.
     */
    fun findSyntaxByFirstLine(s: String): SyntaxReference? {
        val clean = s.removePrefix("\uFEFF")
        for (syntax in syntaxes.asReversed()) {
            val matchPattern = syntax.firstLineMatch ?: continue
            try {
                if (Regex(matchPattern).containsMatchIn(clean)) {
                    return syntax
                }
            } catch (_: Exception) {
                // Invalid regex pattern ignored
            }
        }
        return null
    }

    /**
     * Searches for a syntax by its path when loaded.
     */
    fun findSyntaxByPath(path: String): SyntaxReference? {
        val slashPath = "/$path"
        return pathSyntaxes
            .asReversed()
            .find { it.path.endsWith(slashPath) || it.path == path }
            ?.let { syntaxes[it.syntaxIndex] }
    }

    companion object {
        fun new(): SyntaxSet = SyntaxSet()
    }
}

/**
 * Builder for constructing a SyntaxSet from SyntaxDefinition objects.
 */
class SyntaxSetBuilder {
    private val syntaxes = mutableListOf<SyntaxDefinition>()
    private val pathSyntaxes = mutableListOf<PathSyntax>()

    fun add(syntax: SyntaxDefinition) {
        syntaxes.add(syntax)
    }

    fun addPlain(
        name: String,
        fileExtensions: List<String> = emptyList(),
        scope: Scope = Scope(),
    ) {
        syntaxes.add(
            SyntaxDefinition(
                name = name,
                fileExtensions = fileExtensions,
                scope = scope,
            ),
        )
    }

    fun build(): SyntaxSet {
        val references =
            syntaxes.map { def ->
                SyntaxReference(
                    name = def.name,
                    fileExtensions = def.fileExtensions,
                    scope = def.scope,
                    firstLineMatch = def.firstLineMatch,
                    hidden = def.hidden,
                    variables = def.variables,
                )
            }
        return SyntaxSet(
            syntaxes = references,
            pathSyntaxes = pathSyntaxes.toList(),
        )
    }
}
