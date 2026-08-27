// port-lint: source parsing/parser.rs
package io.github.kotlinmania.syntect.parsing

/**
 * An operation applied to the scope stack at a character offset.
 */
data class ParseOp(
    val offset: Int,
    val op: ScopeStackOp,
)

/**
 * State maintained between parsing lines of a file.
 */
class ParseState(
    val syntax: SyntaxReference? = null,
    val scopeStack: ScopeStack = ScopeStack(),
) {
    /**
     * Parses a single line and returns the operations applied to the scope stack at character indices.
     */
    fun parseLine(
        line: String,
        syntaxSet: SyntaxSet,
    ): List<ParseOp> {
        val ops = mutableListOf<ParseOp>()
        if (syntax != null && syntax.scope.len() > 0) {
            ops.add(ParseOp(0, ScopeStackOp.Push(syntax.scope)))
            ops.add(ParseOp(line.length, ScopeStackOp.Pop(1)))
        } else {
            ops.add(ParseOp(line.length, ScopeStackOp.Noop))
        }
        return ops
    }
}
