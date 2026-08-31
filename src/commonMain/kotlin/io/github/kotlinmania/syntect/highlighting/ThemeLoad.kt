// port-lint: source syntect/src/highlighting/theme_load.rs
package io.github.kotlinmania.syntect.highlighting

import io.github.kotlinmania.syntect.ThemeException

/**
 * An error parsing a theme file.
 */
class ParseThemeError(
    message: String,
    cause: Throwable? = null,
) : ThemeException(message, cause)
