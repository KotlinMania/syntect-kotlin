// port-lint: source syntect/src/highlighting/highlighter.rs
package io.github.kotlinmania.syntect.highlighting

import io.github.kotlinmania.syntect.parsing.ATOM_LEN_BITS
import io.github.kotlinmania.syntect.parsing.BasicScopeStackOp
import io.github.kotlinmania.syntect.parsing.MatchPower
import io.github.kotlinmania.syntect.parsing.ParseOp
import io.github.kotlinmania.syntect.parsing.Scope
import io.github.kotlinmania.syntect.parsing.ScopeStack
import io.github.kotlinmania.syntect.parsing.ScopeStackOp
import kotlin.math.pow

/**
 * A color paired with its match score.
 */
data class ScoredColor(
    val power: MatchPower,
    val color: Color,
)

/**
 * A font style paired with its match score.
 */
data class ScoredFontStyle(
    val power: MatchPower,
    val fontStyle: FontStyle,
)

/**
 * Scored style components representing the highest-scoring theme matches.
 */
data class ScoredStyle(
    var foreground: ScoredColor,
    var background: ScoredColor,
    var fontStyle: ScoredFontStyle,
) {
    fun apply(
        other: StyleModifier,
        score: MatchPower,
    ) {
        if (score > foreground.power) {
            other.foreground?.let { foreground = ScoredColor(score, it) }
        }
        if (score > background.power) {
            other.background?.let { background = ScoredColor(score, it) }
        }
        if (score > fontStyle.power) {
            other.fontStyle?.let { fontStyle = ScoredFontStyle(score, it) }
        }
    }

    fun toStyle(): Style =
        Style(
            foreground = foreground.color,
            background = background.color,
            fontStyle = fontStyle.fontStyle,
        )

    companion object {
        fun fromStyle(style: Style): ScoredStyle =
            ScoredStyle(
                foreground = ScoredColor(MatchPower(-1.0), style.foreground),
                background = ScoredColor(MatchPower(-1.0), style.background),
                fontStyle = ScoredFontStyle(MatchPower(-1.0), style.fontStyle),
            )
    }
}

/**
 * Single scope selector rule paired with a style modifier.
 */
data class SingleSelector(
    val scope: Scope,
    val modifier: StyleModifier,
)

/**
 * Multi scope selector rule paired with a style modifier.
 */
data class MultiSelector(
    val selector: ScopeSelector,
    val modifier: StyleModifier,
)

/**
 * Wrapper around a Theme preparing it for highlighting.
 */
class Highlighter(
    val theme: Theme,
) {
    val singleSelectors: List<SingleSelector>
    val multiSelectors: List<MultiSelector>

    init {
        val singles = mutableListOf<SingleSelector>()
        val multis = mutableListOf<MultiSelector>()
        for (item in theme.scopes) {
            for (sel in item.scope.selectors) {
                val scope = sel.extractSingleScope()
                if (scope != null) {
                    singles.add(SingleSelector(scope, item.style))
                } else {
                    multis.add(MultiSelector(sel, item.style))
                }
            }
        }
        singles.sortByDescending { it.scope.len() }
        singleSelectors = singles
        multiSelectors = multis
    }

    /**
     * The default style in the absence of any matched rules.
     */
    fun getDefault(): Style =
        Style(
            foreground = theme.settings.foreground ?: Color.BLACK,
            background = theme.settings.background ?: Color.WHITE,
            fontStyle = FontStyle.empty(),
        )

    fun updateSingleCacheForPush(
        cur: ScoredStyle,
        path: List<Scope>,
    ): ScoredStyle {
        val newStyle = cur.copy()
        val lastScope = path.last()
        for (rule in singleSelectors) {
            if (rule.scope.isPrefixOf(lastScope)) {
                val singleScore =
                    rule.scope.len().toDouble() *
                        2.0.pow((ATOM_LEN_BITS * (path.size - 1)).toDouble())
                newStyle.apply(rule.modifier, MatchPower(singleScore))
            }
        }
        return newStyle
    }

    fun finalizeStyleWithMultis(
        cur: ScoredStyle,
        path: List<Scope>,
    ): Style {
        val newStyle = cur.copy()
        for (rule in multiSelectors) {
            val score = rule.selector.doesMatch(path)
            if (score != null) {
                newStyle.apply(rule.modifier, score)
            }
        }
        return newStyle.toStyle()
    }

    /**
     * Returns the fully resolved style for the given stack.
     */
    fun styleForStack(stack: List<Scope>): Style {
        var singleCache = ScoredStyle.fromStyle(getDefault())
        for (i in stack.indices) {
            singleCache = updateSingleCacheForPush(singleCache, stack.subList(0, i + 1))
        }
        return finalizeStyleWithMultis(singleCache, stack)
    }

    /**
     * Returns a StyleModifier which, if applied to the default style, yields the resolved style.
     */
    fun styleModForStack(path: List<Scope>): StyleModifier {
        val matchingItems =
            theme.scopes
                .mapNotNull { item ->
                    item.scope.doesMatch(path)?.let { score -> Pair(score, item) }
                }.sortedBy { it.first }

        var modifier = StyleModifier()
        for ((_, item) in matchingItems) {
            modifier = modifier.apply(item.style)
        }
        return modifier
    }
}

/**
 * State maintained between highlighting lines of text.
 */
class HighlightState internal constructor(
    internal val internalStyles: MutableList<Style>,
    internal val internalSingleCaches: MutableList<ScoredStyle>,
    val path: ScopeStack,
) {
    val styles: List<Style> get() = internalStyles.toList()
    val singleCaches: List<ScoredStyle> get() = internalSingleCaches.toList()

    companion object {
        fun new(
            highlighter: Highlighter,
            initialStack: ScopeStack,
        ): HighlightState {
            val styles = mutableListOf(highlighter.getDefault())
            val singleCaches = mutableListOf(ScoredStyle.fromStyle(styles[0]))
            for (i in 0 until initialStack.len()) {
                val prefix = initialStack.bottomN(i + 1)
                val newCache = highlighter.updateSingleCacheForPush(singleCaches[i], prefix)
                styles.add(highlighter.finalizeStyleWithMultis(newCache, prefix))
                singleCaches.add(newCache)
            }
            return HighlightState(
                internalStyles = styles,
                internalSingleCaches = singleCaches,
                path = initialStack,
            )
        }
    }
}

/**
 * A token with its style and character range within the line.
 */
data class RangedToken(
    val style: Style,
    val text: String,
    val range: IntRange,
)

/**
 * Iterator yielding styled text slices along with their source range.
 */
class RangedHighlightIterator(
    val state: HighlightState,
    val changes: List<ParseOp>,
    val text: String,
    val highlighter: Highlighter,
) : Iterator<RangedToken> {
    private var index: Int = 0
    private var pos: Int = 0

    override fun hasNext(): Boolean = !(pos >= text.length && index >= changes.size)

    override fun next(): RangedToken {
        if (!hasNext()) throw NoSuchElementException()
        val (end, command) =
            if (index < changes.size) {
                Pair(changes[index].offset, changes[index].op)
            } else {
                Pair(text.length, ScopeStackOp.Noop)
            }
        val style = state.internalStyles.lastOrNull() ?: Style.default()
        val tokenText = text.substring(pos, end)
        val range = pos until end

        state.path.applyWithHook(command) { op, curStack ->
            when (op) {
                is BasicScopeStackOp.Push -> {
                    val prevCache =
                        state.internalSingleCaches.lastOrNull()
                            ?: ScoredStyle.fromStyle(highlighter.getDefault())
                    val newCache = highlighter.updateSingleCacheForPush(prevCache, curStack)
                    state.internalStyles.add(highlighter.finalizeStyleWithMultis(newCache, curStack))
                    state.internalSingleCaches.add(newCache)
                }
                is BasicScopeStackOp.Pop -> {
                    if (state.internalStyles.isNotEmpty()) {
                        state.internalStyles.removeAt(state.internalStyles.size - 1)
                    }
                    if (state.internalSingleCaches.isNotEmpty()) {
                        state.internalSingleCaches.removeAt(state.internalSingleCaches.size - 1)
                    }
                }
            }
        }

        pos = end
        index++
        return if (tokenText.isEmpty() && hasNext()) {
            next()
        } else {
            RangedToken(style, tokenText, range)
        }
    }
}

/**
 * Iterator yielding styled text tokens.
 */
class HighlightIterator(
    private val rangedIterator: RangedHighlightIterator,
) : Iterator<HighlightedSegment> {
    constructor(
        state: HighlightState,
        changes: List<ParseOp>,
        text: String,
        highlighter: Highlighter,
    ) : this(RangedHighlightIterator(state, changes, text, highlighter))

    override fun hasNext(): Boolean = rangedIterator.hasNext()

    override fun next(): HighlightedSegment {
        if (!hasNext()) throw NoSuchElementException()
        val (style, text, _) = rangedIterator.next()
        return HighlightedSegment(style, text)
    }
}
