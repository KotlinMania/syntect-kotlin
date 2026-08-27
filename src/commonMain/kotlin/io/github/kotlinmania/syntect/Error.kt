// port-lint: source syntect/src/lib.rs
package io.github.kotlinmania.syntect

/**
 * Common exception type used throughout syntect.
 */
open class SyntectException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * An error enum for all things that can go wrong within syntect.
 */
sealed class Error(
    message: String,
    cause: Throwable? = null,
) : SyntectException(message, cause) {
    /**
     * An error occurred while loading a syntax or theme.
     */
    data class LoadingError(
        val error: io.github.kotlinmania.syntect.LoadingError,
    ) : Error(error.message ?: "Loading error", error)

    /**
     * An error occurred while parsing.
     */
    data class ParsingError(
        val error: ParsingException,
    ) : Error(error.message ?: "Parsing error", error)

    /**
     * Scope error.
     */
    data class ScopeError(
        val error: ScopeException,
    ) : Error(error.message ?: "Scope error", error)

    /**
     * Formatting error.
     */
    data class Fmt(
        val error: Throwable,
    ) : Error(error.message ?: "Formatting error", error)

    /**
     * IO Error.
     */
    data class Io(
        val error: Throwable,
    ) : Error(error.message ?: "IO error", error)
}

/**
 * Common error type used by syntax and theme loading.
 */
sealed class LoadingError(
    message: String,
    cause: Throwable? = null,
) : SyntectException(message, cause) {
    data class Io(
        val error: Throwable,
    ) : LoadingError(error.message ?: "IO error", error)

    data class ParseSyntax(
        val syntaxName: String,
        val details: String,
    ) : LoadingError("$syntaxName: $details")

    data class ParseMetadata(
        val details: String,
    ) : LoadingError("Failed to parse JSON: $details")

    data class ParseTheme(
        val details: String,
    ) : LoadingError("Invalid syntax theme: $details")

    data class ReadSettings(
        val details: String,
    ) : LoadingError("Invalid syntax theme settings: $details")

    data object BadPath : LoadingError("Invalid path")
}

/**
 * Error occurred during scope parsing or manipulation.
 */
open class ScopeException(
    message: String,
    cause: Throwable? = null,
) : SyntectException(message, cause)

/**
 * Error occurred during theme parsing or loading.
 */
open class ThemeException(
    message: String,
    cause: Throwable? = null,
) : SyntectException(message, cause)

/**
 * Error occurred during syntax parsing.
 */
open class ParsingException(
    message: String,
    cause: Throwable? = null,
) : SyntectException(message, cause)
