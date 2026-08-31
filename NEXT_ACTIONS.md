# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 23/23 (100.0%)
- **Function parity:** 198/374 matched (target 263) — 52.9%
- **Class/type parity:** 42/83 matched (target 91) — 50.6%
- **Combined symbol parity:** 240/457 matched (target 354) — 52.5%
- **Average inline-code cosine:** 0.25 (function body across 18 matched files)
- **Average documentation cosine:** 0.43 (doc text across 18 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 23 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. parsing.syntax_set

- **Target:** `parsing.SyntaxSet [PROVENANCE-FALLBACK]`
- **Similarity:** 0.26
- **Dependents:** 4
- **Priority Score:** 4356207.5
- **Functions:** 25/57 matched (target 32)
- **Missing functions:** `load_syntax_file`, `clone`, `default`, `load_from_folder`, `syntaxes`, `set_metadata`, `metadata`, `find_syntax_for_file`, `find_syntax_plain_text`, `into_builder`, `get_context`, `first_line_cache`, `find_unlinked_contexts`, `find_unlinked_contexts_in_context`, `context_ids`, `contexts`, `lazy_contexts`, `deserialize`, `add_plain_text_syntax`, `add_from_folder`, `recursively_mark_no_prototype`, `link_context`, `link_ref`, `with_plain_text_fallback`, `find_id`, `link_match_pat`, `assert_ops_contain`, `assert_prototype_only_on`, `check_send`, `check_sync`, `syntax_a`, `syntax_b`
- **Types:** 2/5 matched (target 4)
- **Missing types:** `SyntaxReference`, `LazyContexts`, `FirstLineCache`
- **Tests:** 16/22 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/parsing/syntax_set.rs` vs expected `parsing/syntax_set.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/parsing/syntax_set.rs` vs expected `parsing/syntax_set.rs`
- **Proposed provenance header:** `// port-lint: source parsing/syntax_set.rs` (current: `// port-lint: source syntect/src/parsing/syntax_set.rs`)
- **Proposed provenance header:** `// port-lint: tests parsing/syntax_set.rs` (current: `// port-lint: tests syntect/src/parsing/syntax_set.rs`)
- **Lint issues:** 2

### 2. parsing.regex

- **Target:** `parsing.Regex [PROVENANCE-FALLBACK]`
- **Similarity:** 0.23
- **Dependents:** 2
- **Priority Score:** 2091807.8
- **Functions:** 7/16 matched (target 17)
- **Missing functions:** `regex_str`, `regex`, `clone`, `eq`, `serialize`, `deserialize`, `default`, `new_region`, `init_from_captures`
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/parsing/regex.rs` vs expected `parsing/regex.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/parsing/regex.rs` vs expected `parsing/regex.rs`
- **Proposed provenance header:** `// port-lint: source parsing/regex.rs` (current: `// port-lint: source syntect/src/parsing/regex.rs`)
- **Proposed provenance header:** `// port-lint: tests parsing/regex.rs` (current: `// port-lint: tests syntect/src/parsing/regex.rs`)
- **Lint issues:** 2

### 3. highlighting.theme_set

- **Target:** `highlighting.ThemeSet [PROVENANCE-FALLBACK]`
- **Similarity:** 0.22
- **Dependents:** 2
- **Priority Score:** 2040807.9
- **Functions:** 3/7 matched (target 4)
- **Missing functions:** `discover_theme_paths`, `load_from_reader`, `load_from_folder`, `add_from_folder`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/highlighting/theme_set.rs` vs expected `highlighting/theme_set.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/highlighting/theme_set.rs` vs expected `highlighting/theme_set.rs`
- **Proposed provenance header:** `// port-lint: source highlighting/theme_set.rs` (current: `// port-lint: source syntect/src/highlighting/theme_set.rs`)
- **Proposed provenance header:** `// port-lint: tests highlighting/theme_set.rs` (current: `// port-lint: tests syntect/src/highlighting/theme_set.rs`)
- **Lint issues:** 2

### 4. escape

- **Target:** `escape.Escape [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2010210.0
- **Functions:** 0/1 matched (target 3)
- **Missing functions:** `fmt`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/escape.rs` vs expected `escape.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/escape.rs` vs expected `escape.rs`
- **Proposed provenance header:** `// port-lint: source escape.rs` (current: `// port-lint: source syntect/src/escape.rs`)
- **Proposed provenance header:** `// port-lint: tests escape.rs` (current: `// port-lint: tests syntect/src/escape.rs`)
- **Lint issues:** 2

### 5. parsing.syntax_definition

- **Target:** `parsing.SyntaxDefinition [PROVENANCE-FALLBACK]`
- **Similarity:** 0.05
- **Dependents:** 1
- **Priority Score:** 1182109.5
- **Functions:** 1/11 matched (target 2)
- **Missing functions:** `new`, `next`, `context_iter`, `match_at`, `resolve`, `id`, `substitute_backrefs_in_regex`, `regex_with_refs`, `regex`, `ordered_map`
- **Types:** 2/10 matched (target 4)
- **Missing types:** `CaptureMapping`, `Context`, `Pattern`, `MatchIter`, `MatchPattern`, `ContextReference`, `MatchOperation`, `Item`
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/parsing/syntax_definition.rs` vs expected `parsing/syntax_definition.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/parsing/syntax_definition.rs` vs expected `parsing/syntax_definition.rs`
- **Proposed provenance header:** `// port-lint: source parsing/syntax_definition.rs` (current: `// port-lint: source syntect/src/parsing/syntax_definition.rs`)
- **Proposed provenance header:** `// port-lint: tests parsing/syntax_definition.rs` (current: `// port-lint: tests syntect/src/parsing/syntax_definition.rs`)
- **Lint issues:** 2

### 6. parsing.scope

- **Target:** `parsing.Scope [PROVENANCE-FALLBACK]`
- **Similarity:** 0.48
- **Dependents:** 1
- **Priority Score:** 1124605.1
- **Functions:** 25/34 matched (target 36)
- **Missing functions:** `lock_global_scope_repo`, `from_str`, `fmt`, `serialize`, `deserialize`, `expecting`, `visit_str`, `cmp`, `debug_print`
- **Types:** 9/12 matched (target 17)
- **Missing types:** `Err`, `ScopeVisitor`, `Value`
- **Tests:** 5/5 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/parsing/scope.rs` vs expected `parsing/scope.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/parsing/scope.rs` vs expected `parsing/scope.rs`
- **Proposed provenance header:** `// port-lint: source parsing/scope.rs` (current: `// port-lint: source syntect/src/parsing/scope.rs`)
- **Proposed provenance header:** `// port-lint: tests parsing/scope.rs` (current: `// port-lint: tests syntect/src/parsing/scope.rs`)
- **Lint issues:** 2

### 7. parsing.yaml_load

- **Target:** `parsing.YamlLoad [PROVENANCE-FALLBACK]`
- **Similarity:** 0.14
- **Dependents:** 0
- **Priority Score:** 284308.6
- **Functions:** 14/36 matched (target 14)
- **Missing functions:** `get_key`, `str_to_scopes`, `load_from_str`, `parse_top_level`, `parse_contexts`, `parse_context`, `parse_reference`, `parse_match_pattern`, `parse_pushargs`, `parse_regex`, `resolve_variables`, `try_compile_regex`, `parse_captures`, `add_initial_contexts`, `new`, `next`, `replace_posix_char_classes`, `regex_for_newlines`, `regex_for_no_newlines`, `get_consuming_capture_indexes`, `peek`, `parse_character_class`
- **Types:** 1/7 matched (target 2)
- **Missing types:** `ParserState`, `ContextNamer`, `RegexRewriterForNewlines`, `RegexRewriterForNoNewlines`, `ConsumingCaptureIndexParser`, `Parser`
- **Tests:** 13/13 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/parsing/yaml_load.rs` vs expected `parsing/yaml_load.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/parsing/yaml_load.rs` vs expected `parsing/yaml_load.rs`
- **Proposed provenance header:** `// port-lint: source parsing/yaml_load.rs` (current: `// port-lint: source syntect/src/parsing/yaml_load.rs`)
- **Proposed provenance header:** `// port-lint: tests parsing/yaml_load.rs` (current: `// port-lint: tests syntect/src/parsing/yaml_load.rs`)
- **Lint issues:** 2

### 8. parsing.metadata

- **Target:** `parsing.Metadata [PROVENANCE-FALLBACK]`
- **Similarity:** 0.14
- **Dependents:** 0
- **Priority Score:** 263708.6
- **Functions:** 7/26 matched (target 8)
- **Missing functions:** `add_raw`, `quick_load`, `from`, `append_vars`, `merged_with_raw`, `from_raw`, `get_line_comment_marker`, `get_block_comment_markers`, `unindented_line`, `decrease_indent`, `increase_indent`, `bracket_increase`, `disable_indent_next_line`, `line_comment`, `block_comment`, `best_match`, `load`, `serialize`, `deserialize`
- **Types:** 4/11 matched (target 7)
- **Missing types:** `Dict`, `SelectorString`, `RawMetadataEntry`, `LoadMetadata`, `KeyPair`, `ShellVars`, `MetaSetSerializable`
- **Tests:** 5/6 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/parsing/metadata.rs` vs expected `parsing/metadata.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/parsing/metadata.rs` vs expected `parsing/metadata.rs`
- **Proposed provenance header:** `// port-lint: source parsing/metadata.rs` (current: `// port-lint: source syntect/src/parsing/metadata.rs`)
- **Proposed provenance header:** `// port-lint: tests parsing/metadata.rs` (current: `// port-lint: tests syntect/src/parsing/metadata.rs`)
- **Lint issues:** 2

### 9. highlighting.style

- **Target:** `highlighting.Style [PROVENANCE-FALLBACK]`
- **Similarity:** 0.21
- **Dependents:** 0
- **Priority Score:** 223807.9
- **Functions:** 12/33 matched (target 19)
- **Missing functions:** `fmt`, `bits`, `from_bits`, `from_bits_truncate`, `from_bits_unchecked`, `intersects`, `insert`, `remove`, `toggle`, `set`, `bitor`, `bitor_assign`, `bitxor`, `bitxor_assign`, `bitand`, `bitand_assign`, `sub`, `sub_assign`, `not`, `extend`, `from_iter`
- **Types:** 4/5 matched (target 6)
- **Missing types:** `Output`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/highlighting/style.rs` vs expected `highlighting/style.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/highlighting/style.rs` vs expected `highlighting/style.rs`
- **Proposed provenance header:** `// port-lint: source highlighting/style.rs` (current: `// port-lint: source syntect/src/highlighting/style.rs`)
- **Proposed provenance header:** `// port-lint: tests highlighting/style.rs` (current: `// port-lint: tests syntect/src/highlighting/style.rs`)
- **Lint issues:** 2

### 10. parsing.parser

- **Target:** `parsing.Parser [PROVENANCE-FALLBACK]`
- **Similarity:** 0.34
- **Dependents:** 0
- **Priority Score:** 186106.6
- **Functions:** 42/56 matched (target 48)
- **Missing functions:** `new`, `parse_next_token`, `find_best_match`, `search`, `exec_pattern`, `push_meta_ops`, `perform_op`, `expect_scope_stacks`, `expect_scope_stacks_with_syntax`, `expect_scope_stacks_for_ops`, `parse`, `link`, `ops`, `stack_states`
- **Types:** 1/5 matched (target 3)
- **Missing types:** `ParsingError`, `StateLevel`, `RegexMatch`, `SearchCache`
- **Tests:** 41/48 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/parsing/parser.rs` vs expected `parsing/parser.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/parsing/parser.rs` vs expected `parsing/parser.rs`
- **Proposed provenance header:** `// port-lint: source parsing/parser.rs` (current: `// port-lint: source syntect/src/parsing/parser.rs`)
- **Proposed provenance header:** `// port-lint: tests parsing/parser.rs` (current: `// port-lint: tests syntect/src/parsing/parser.rs`)
- **Lint issues:** 2

### 11. dumps

- **Target:** `syntect.Dumps [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 141709.1
- **Functions:** 3/17 matched (target 3)
- **Missing functions:** `dump_to_writer`, `dump_binary`, `dump_to_file`, `from_reader`, `from_binary`, `from_dump_file`, `dump_to_uncompressed_file`, `from_uncompressed_dump_file`, `from_uncompressed_data`, `serialize_to_writer_impl`, `deserialize_from_reader_impl`, `load_defaults_nonewlines`, `load_defaults_newlines`, `load_defaults`
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Tests:** 3/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/dumps.rs` vs expected `dumps.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/dumps.rs` vs expected `dumps.rs`
- **Proposed provenance header:** `// port-lint: source dumps.rs` (current: `// port-lint: source syntect/src/dumps.rs`)
- **Proposed provenance header:** `// port-lint: tests dumps.rs` (current: `// port-lint: tests syntect/src/dumps.rs`)
- **Lint issues:** 2

### 12. util

- **Target:** `util.Util [PROVENANCE-FALLBACK]`
- **Similarity:** 0.38
- **Dependents:** 0
- **Priority Score:** 81506.2
- **Functions:** 7/13 matched (target 10)
- **Missing functions:** `as_latex_escaped`, `textcolor`, `debug_print_ops`, `from`, `next`, `lines`
- **Types:** 0/2 matched
- **Missing types:** `LinesWithEndings`, `Item`
- **Tests:** 3/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/util.rs` vs expected `util.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/util.rs` vs expected `util.rs`
- **Proposed provenance header:** `// port-lint: source util.rs` (current: `// port-lint: source syntect/src/util.rs`)
- **Proposed provenance header:** `// port-lint: tests util.rs` (current: `// port-lint: tests syntect/src/util.rs`)
- **Lint issues:** 2

### 13. html

- **Target:** `html.Html [PROVENANCE-FALLBACK]`
- **Similarity:** 0.44
- **Dependents:** 0
- **Priority Score:** 63105.6
- **Functions:** 22/28 matched (target 24)
- **Missing functions:** `new`, `parse_html_for_line`, `css_for_theme`, `highlighted_html_for_file`, `tokens_to_classed_spans`, `tokens_to_classed_html`
- **Types:** 3/3 matched (target 11)
- **Missing types:** _none_
- **Tests:** 9/9 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/html.rs` vs expected `html.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/html.rs` vs expected `html.rs`
- **Proposed provenance header:** `// port-lint: source html.rs` (current: `// port-lint: source syntect/src/html.rs`)
- **Proposed provenance header:** `// port-lint: tests html.rs` (current: `// port-lint: tests syntect/src/html.rs`)
- **Lint issues:** 2

### 14. easy

- **Target:** `easy.HighlightLines [PROVENANCE-FALLBACK]`
- **Similarity:** 0.47
- **Dependents:** 0
- **Priority Score:** 41605.3
- **Functions:** 9/11 matched (target 14)
- **Missing functions:** `new`, `highlight`
- **Types:** 3/5 matched (target 7)
- **Missing types:** `HighlightFile`, `Item`
- **Tests:** 5/5 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/easy.rs` vs expected `easy.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/easy.rs` vs expected `easy.rs`
- **Proposed provenance header:** `// port-lint: source easy.rs` (current: `// port-lint: source syntect/src/easy.rs`)
- **Proposed provenance header:** `// port-lint: tests easy.rs` (current: `// port-lint: tests syntect/src/easy.rs`)
- **Lint issues:** 2

### 15. highlighting.theme_load

- **Target:** `highlighting.ThemeLoad [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 40510.0
- **Functions:** 0/2 matched (target 0)
- **Missing functions:** `from_str`, `parse_settings`
- **Types:** 1/3 matched (target 1)
- **Missing types:** `Err`, `Error`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/highlighting/theme_load.rs` vs expected `highlighting/theme_load.rs`
- **Proposed provenance header:** `// port-lint: source highlighting/theme_load.rs` (current: `// port-lint: source syntect/src/highlighting/theme_load.rs`)
- **Lint issues:** 1

### 16. highlighting.settings

- **Target:** `highlighting.Settings [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 30410.0
- **Functions:** 0/2 matched (target 0)
- **Missing functions:** `from`, `read_plist`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `ParseSettings`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/highlighting/settings.rs` vs expected `highlighting/settings.rs`
- **Proposed provenance header:** `// port-lint: source highlighting/settings.rs` (current: `// port-lint: source syntect/src/highlighting/settings.rs`)
- **Lint issues:** 1

### 17. highlighting.highlighter

- **Target:** `highlighting.Highlighter [PROVENANCE-FALLBACK]`
- **Similarity:** 0.58
- **Dependents:** 0
- **Priority Score:** 22104.2
- **Functions:** 14/15 matched (target 19)
- **Missing functions:** `update_scored`
- **Types:** 5/6 matched (target 11)
- **Missing types:** `Item`
- **Tests:** 4/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/highlighting/highlighter.rs` vs expected `highlighting/highlighter.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/highlighting/highlighter.rs` vs expected `highlighting/highlighter.rs`
- **Proposed provenance header:** `// port-lint: source highlighting/highlighter.rs` (current: `// port-lint: source syntect/src/highlighting/highlighter.rs`)
- **Proposed provenance header:** `// port-lint: tests highlighting/highlighter.rs` (current: `// port-lint: tests syntect/src/highlighting/highlighter.rs`)
- **Lint issues:** 2

### 18. highlighting.selector

- **Target:** `highlighting.Selector [PROVENANCE-FALLBACK]`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 21104.8
- **Functions:** 7/8 matched (target 10)
- **Missing functions:** `from_str`
- **Types:** 2/3 matched
- **Missing types:** `Err`
- **Tests:** 4/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/highlighting/selector.rs` vs expected `highlighting/selector.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:syntect/src/highlighting/selector.rs` vs expected `highlighting/selector.rs`
- **Proposed provenance header:** `// port-lint: source highlighting/selector.rs` (current: `// port-lint: source syntect/src/highlighting/selector.rs`)
- **Proposed provenance header:** `// port-lint: tests highlighting/selector.rs` (current: `// port-lint: tests syntect/src/highlighting/selector.rs`)
- **Lint issues:** 2

### 19. utils

- **Target:** `util.Utils [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10110.0
- **Functions:** 0/1 matched (target 0)
- **Missing functions:** `walk_dir`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `syntect/src/utils.rs` vs expected `utils.rs`
- **Proposed provenance header:** `// port-lint: source utils.rs` (current: `// port-lint: source syntect/src/utils.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Matched

| Source | Target | Path |
|--------|--------|------|
| `highlighting.theme` | `highlighting.Theme` | `highlighting/theme` |
| `lib` | `syntect.Mod` | `lib` |
| `parsing.mod` | `parsing.Mod` | `parsing/mod` |
| `highlighting.mod` | `highlighting.Mod` | `highlighting/mod` |

