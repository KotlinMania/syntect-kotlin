// port-lint: source syntect/src/highlighting/settings.rs
package io.github.kotlinmania.syntect.highlighting

import io.github.kotlinmania.syntect.SyntectException

/**
 * An error parsing a settings file.
 */
class SettingsError(
    message: String,
    cause: Throwable? = null,
) : SyntectException(message, cause)
