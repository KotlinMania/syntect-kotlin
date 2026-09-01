// port-lint: source parsing/scope.rs
package io.github.kotlinmania.syntect.parsing

import io.github.kotlinmania.syntect.ScopeException
import kotlin.math.pow
import kotlin.text.Regex

const val ATOM_LEN_BITS: Int = 3

/**
 * Score representing the strength of a scope selector match.
 */
data class MatchPower(
    val value: Double,
) : Comparable<MatchPower> {
    override fun compareTo(other: MatchPower): Int = this.value.compareTo(other.value)
}

/**
 * Scope error types.
 */
enum class ScopeError {
    NoClearedScopesToRestore,
}

/**
 * Scope parsing error types.
 */
enum class ParseScopeError {
    TooLong,
    TooManyAtoms,
}

/**
 * Hierarchy of atoms representing semantic scopes.
 */
data class Scope(
    val a: ULong = 0uL,
    val b: ULong = 0uL,
) : Comparable<Scope> {
    fun atomAt(index: Int): UShort {
        require(index in 0..7) { "Atom index out of bounds: $index" }
        val shifted =
            if (index < 4) {
                this.a shr ((3 - index) * 16)
            } else {
                this.b shr ((7 - index) * 16)
            }
        return (shifted and 0xFFFFuL).toUShort()
    }

    fun missingAtoms(): Int {
        val trail =
            if (this.b == 0uL) {
                this.a.countTrailingZeroBits() + 64
            } else {
                this.b.countTrailingZeroBits()
            }
        return trail / 16
    }

    fun len(): Int = 8 - missingAtoms()

    fun isEmpty(): Boolean = len() == 0

    fun buildString(repo: ScopeRepository = globalScopeRepo): String = repo.toString(this)

    fun isPrefixOf(other: Scope): Boolean {
        val prefMissing = this.missingAtoms()
        val maskA: ULong
        val maskB: ULong
        when {
            prefMissing == 8 -> {
                maskA = 0uL
                maskB = 0uL
            }
            prefMissing == 4 -> {
                maskA = ULong.MAX_VALUE
                maskB = 0uL
            }
            prefMissing > 4 -> {
                maskA = ULong.MAX_VALUE shl ((prefMissing - 4) * 16)
                maskB = 0uL
            }
            else -> {
                maskA = ULong.MAX_VALUE
                maskB = ULong.MAX_VALUE shl (prefMissing * 16)
            }
        }
        val ax = (this.a xor other.a) and maskA
        val bx = (this.b xor other.b) and maskB
        return ax == 0uL && bx == 0uL
    }

    override fun compareTo(other: Scope): Int {
        val cmpA = this.a.compareTo(other.a)
        if (cmpA != 0) return cmpA
        return this.b.compareTo(other.b)
    }

    override fun toString(): String = buildString()

    companion object {
        val globalScopeRepo: ScopeRepository = ScopeRepository()

        fun new(s: String): Scope = globalScopeRepo.build(s)

        fun fromString(s: String): Scope = new(s)
    }
}

private fun packAsU16s(atoms: List<Int>): Scope {
    var a = 0uL
    var b = 0uL
    for ((i, n) in atoms.withIndex()) {
        if (n >= 0xFFFF - 2) {
            throw ScopeException("Too many atoms. Max 65533 atoms allowed.")
        }
        val small = (n + 1).toULong()
        if (i < 4) {
            val shift = (3 - i) * 16
            a = a or (small shl shift)
        } else {
            val shift = (7 - i) * 16
            b = b or (small shl shift)
        }
    }
    return Scope(a, b)
}

/**
 * Mapping between scope atom numbers and their string names.
 */
class ScopeRepository {
    private val atoms = mutableListOf<String>()
    private val atomIndexMap = mutableMapOf<String, Int>()

    fun build(s: String): Scope {
        val trimmed = s.trim()
        if (trimmed.isEmpty()) {
            return Scope(0uL, 0uL)
        }
        val parts = trimmed.trimEnd('.').split('.')
        if (parts.size > 8) {
            throw ScopeException("Too long scope. Scopes can be at most 8 atoms long: $s")
        }
        val atomIndices = parts.map { atomToIndex(it) }
        return packAsU16s(atomIndices)
    }

    fun toString(scope: Scope): String {
        val sb = StringBuilder()
        for (i in 0..7) {
            val atomNumber = scope.atomAt(i).toInt()
            if (atomNumber == 0) break
            if (i != 0) sb.append('.')
            sb.append(atomStr(atomNumber))
        }
        return sb.toString()
    }

    fun atomToIndex(atom: String): Int {
        val existing = atomIndexMap[atom]
        if (existing != null) return existing
        val index = atoms.size
        atoms.add(atom)
        atomIndexMap[atom] = index
        return index
    }

    fun atomStr(atomNumber: Int): String {
        require(atomNumber in 1..atoms.size) { "Invalid atom number: $atomNumber" }
        return atoms[atomNumber - 1]
    }
}

/**
 * Amount of scopes to clear from a ScopeStack.
 */
sealed class ClearAmount {
    data class TopN(
        val count: Int,
    ) : ClearAmount()

    data object All : ClearAmount()
}

/**
 * A change to a ScopeStack.
 */
sealed class ScopeStackOp {
    data class Push(
        val scope: Scope,
    ) : ScopeStackOp()

    data class Pop(
        val count: Int,
    ) : ScopeStackOp()

    data class Clear(
        val amount: ClearAmount,
    ) : ScopeStackOp()

    data object Restore : ScopeStackOp()

    data object Noop : ScopeStackOp()
}

/**
 * Primitive scope stack operation for apply hooks.
 */
sealed class BasicScopeStackOp {
    data class Push(
        val scope: Scope,
    ) : BasicScopeStackOp()

    data object Pop : BasicScopeStackOp()
}

/**
 * Stack of scopes for representing token hierarchies.
 */
class ScopeStack(
    scopes: List<Scope> = emptyList(),
) {
    private val _scopes = scopes.toMutableList()
    val scopes: List<Scope> get() = _scopes

    private val clearStack = mutableListOf<MutableList<Scope>>()

    fun push(scope: Scope) {
        _scopes.add(scope)
    }

    fun pop(): Scope? = if (_scopes.isNotEmpty()) _scopes.removeAt(_scopes.size - 1) else null

    fun apply(op: ScopeStackOp) {
        applyWithHook(op) { _, _ -> }
    }

    fun applyWithHook(
        op: ScopeStackOp,
        hook: (BasicScopeStackOp, List<Scope>) -> Unit,
    ) {
        when (op) {
            is ScopeStackOp.Push -> {
                _scopes.add(op.scope)
                hook(BasicScopeStackOp.Push(op.scope), _scopes)
            }
            is ScopeStackOp.Pop -> {
                repeat(op.count) {
                    if (_scopes.isNotEmpty()) {
                        _scopes.removeAt(_scopes.size - 1)
                        hook(BasicScopeStackOp.Pop, _scopes)
                    }
                }
            }
            is ScopeStackOp.Clear -> {
                val cleared =
                    when (val amount = op.amount) {
                        is ClearAmount.TopN -> {
                            val toLeave = (_scopes.size - minOf(amount.count, _scopes.size)).coerceAtLeast(0)
                            val removed = _scopes.subList(toLeave, _scopes.size).toMutableList()
                            while (_scopes.size > toLeave) {
                                _scopes.removeAt(_scopes.size - 1)
                            }
                            removed
                        }
                        is ClearAmount.All -> {
                            val removed = _scopes.toMutableList()
                            _scopes.clear()
                            removed
                        }
                    }
                val clearAmount = cleared.size
                clearStack.add(cleared)
                repeat(clearAmount) {
                    hook(BasicScopeStackOp.Pop, _scopes)
                }
            }
            is ScopeStackOp.Restore -> {
                if (clearStack.isEmpty()) {
                    throw ScopeException("Tried to restore cleared scopes, but none were cleared")
                }
                val toPush = clearStack.removeAt(clearStack.size - 1)
                for (s in toPush) {
                    _scopes.add(s)
                    hook(BasicScopeStackOp.Push(s), _scopes)
                }
            }
            is ScopeStackOp.Noop -> Unit
        }
    }

    fun bottomN(n: Int): List<Scope> = _scopes.subList(0, n.coerceAtMost(_scopes.size))

    fun asSlice(): List<Scope> = _scopes

    fun len(): Int = _scopes.size

    fun isEmpty(): Boolean = _scopes.isEmpty()

    fun doesMatch(stack: List<Scope>): MatchPower? {
        var selIndex = 0
        var score = 0.0
        for ((i, scope) in stack.withIndex()) {
            val selScope = _scopes[selIndex]
            if (selScope.isPrefixOf(scope)) {
                val len = selScope.len()
                score += len.toDouble() * 2.0.pow((ATOM_LEN_BITS * i).toDouble())
                selIndex++
                if (selIndex >= _scopes.size) {
                    return MatchPower(score)
                }
            }
        }
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScopeStack) return false
        return _scopes == other._scopes
    }

    override fun hashCode(): Int = _scopes.hashCode()

    override fun toString(): String = _scopes.joinToString(" ") { it.toString() }

    companion object {
        fun fromVec(v: List<Scope>): ScopeStack = ScopeStack(v)

        fun fromString(s: String): ScopeStack {
            val trimmed = s.trim()
            if (trimmed.isEmpty()) return ScopeStack()
            val scopes = trimmed.split(Regex("\\s+")).filter { it.isNotEmpty() }.map { Scope.new(it) }
            return ScopeStack(scopes)
        }
    }
}
