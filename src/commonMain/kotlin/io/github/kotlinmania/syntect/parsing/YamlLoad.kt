// port-lint: source syntect/src/parsing/yaml_load.rs
package io.github.kotlinmania.syntect.parsing

import io.github.kotlinmania.syntect.ParsingException

/**
 * An error parsing a syntax definition file.
 */
class ParseSyntaxError(
    message: String,
    cause: Throwable? = null,
) : ParsingException(message, cause)
