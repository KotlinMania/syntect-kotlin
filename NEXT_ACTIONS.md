# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 23/23 (100.0%)
- **Function parity:** 88/374 matched (target 154) — 23.5%
- **Class/type parity:** 48/89 matched (target 106) — 53.9%
- **Combined symbol parity:** 136/463 matched (target 260) — 29.4%
- **Average inline-code cosine:** 0.20 (function body across 19 matched files)
- **Average documentation cosine:** 0.42 (doc text across 19 matched files)
- **Cheat-zeroed Files:** 9
- **Critical Issues:** 22 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. parsing.syntax_set

- **Target:** `parsing.SyntaxSet`
- **Similarity:** 0.10
- **Dependents:** 4
- **Priority Score:** 4516209.0
- **Functions:** 9/57 matched (target 10)
- **Missing functions:** `load_syntax_file`, `clone`, `default`, `load_from_folder`, `syntaxes`, `set_metadata`, `metadata`, `find_syntax_for_file`, `find_syntax_plain_text`, `into_builder`, `get_context`, `first_line_cache`, `find_unlinked_contexts`, `find_unlinked_contexts_in_context`, `context_ids`, `contexts`, `lazy_contexts`, `deserialize`, `add_plain_text_syntax`, `add_from_folder`, `recursively_mark_no_prototype`, `link_context`, `link_ref`, `with_plain_text_fallback`, `find_id`, `link_match_pat`, `can_load`, `can_clone`, `can_list_added_syntaxes`, `can_add_more_syntaxes_with_builder`, `falls_back_to_plain_text_when_embedded_scope_is_missing`, `falls_back_to_plain_text_when_embedded_file_is_missing`, `test_plain_text_fallback`, `can_find_unlinked_contexts`, `can_use_in_multiple_threads`, `is_sync`, `is_send`, `can_override_syntaxes`, `can_parse_issue219`, `no_prototype_for_contexts_included_from_prototype`, `no_prototype_for_contexts_inline_in_prototype`, `find_syntax_set_from_line_with_bom`, `assert_ops_contain`, `assert_prototype_only_on`, `check_send`, `check_sync`, `syntax_a`, `syntax_b`
- **Types:** 2/5 matched (target 3)
- **Missing types:** `SyntaxReference`, `LazyContexts`, `FirstLineCache`
- **Tests:** 0/22 matched

### 2. parsing.regex

- **Target:** `parsing.Regex`
- **Similarity:** 0.17
- **Dependents:** 2
- **Priority Score:** 2111808.2
- **Functions:** 5/16 matched (target 15)
- **Missing functions:** `regex_str`, `regex`, `clone`, `eq`, `serialize`, `deserialize`, `default`, `new_region`, `init_from_captures`, `caches_compiled_regex`, `serde_as_string`
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Tests:** 0/2 matched

### 3. highlighting.theme_set

- **Target:** `highlighting.ThemeSet`
- **Similarity:** 0.14
- **Dependents:** 2
- **Priority Score:** 2050808.6
- **Functions:** 2/7 matched (target 3)
- **Missing functions:** `discover_theme_paths`, `load_from_reader`, `load_from_folder`, `add_from_folder`, `can_parse_common_themes`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Tests:** 0/1 matched

### 4. escape

- **Target:** `escape.Escape`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2010210.0
- **Functions:** 0/1 matched (target 3)
- **Missing functions:** `fmt`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 5. parsing.syntax_definition

- **Target:** `parsing.SyntaxDefinition [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1192110.0
- **Functions:** 0/11 matched (target 0)
- **Missing functions:** `new`, `next`, `context_iter`, `match_at`, `resolve`, `id`, `substitute_backrefs_in_regex`, `regex_with_refs`, `regex`, `ordered_map`, `can_compile_refs`
- **Types:** 2/10 matched (target 3)
- **Missing types:** `CaptureMapping`, `Context`, `Pattern`, `MatchIter`, `MatchPattern`, `ContextReference`, `MatchOperation`, `Item`
- **Tests:** 0/1 matched

### 6. parsing.scope

- **Target:** `parsing.Scope`
- **Similarity:** 0.41
- **Dependents:** 1
- **Priority Score:** 1174605.9
- **Functions:** 20/34 matched (target 35)
- **Missing functions:** `lock_global_scope_repo`, `from_str`, `fmt`, `serialize`, `deserialize`, `expecting`, `visit_str`, `cmp`, `debug_print`, `misc`, `repo_works`, `global_repo_works`, `prefixes_work`, `matching_works`
- **Types:** 9/12 matched (target 17)
- **Missing types:** `Err`, `ScopeVisitor`, `Value`
- **Tests:** 0/5 matched

### 7. highlighting.theme

- **Target:** `highlighting.Theme [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1000410.0
- **Functions:** 0/0 matched (target 1)
- **Missing functions:** _none_
- **Types:** 4/4 matched
- **Missing types:** _none_

### 8. parsing.parser

- **Target:** `parsing.Parser`
- **Similarity:** 0.01
- **Dependents:** 0
- **Priority Score:** 596109.9
- **Functions:** 1/56 matched (target 1)
- **Missing functions:** `new`, `parse_next_token`, `find_best_match`, `search`, `exec_pattern`, `push_meta_ops`, `perform_op`, `can_parse_simple`, `can_parse_yaml`, `can_parse_includes`, `can_parse_backrefs`, `can_parse_preprocessor_rules`, `can_parse_issue25`, `can_compare_parse_states`, `can_parse_non_nested_clear_scopes`, `can_parse_non_nested_too_many_clear_scopes`, `can_parse_nested_clear_scopes`, `can_parse_infinite_loop`, `can_parse_infinite_seeming_loop`, `can_parse_prototype_that_pops_main`, `can_parse_syntax_with_newline_in_character_class`, `can_parse_issue120`, `can_parse_non_consuming_pop_that_would_loop`, `can_parse_non_consuming_set_and_pop_that_would_loop`, `can_parse_non_consuming_set_after_consuming_push_that_does_not_loop`, `can_parse_non_consuming_set_after_consuming_set_that_does_not_loop`, `can_parse_non_consuming_pop_that_would_loop_at_end_of_line`, `can_parse_empty_but_consuming_set_that_does_not_loop`, `can_parse_non_consuming_pop_that_does_not_loop`, `can_parse_non_consuming_pop_with_multi_push_that_does_not_loop`, `can_parse_non_consuming_pop_of_recursive_context_that_does_not_loop`, `can_parse_non_consuming_pop_order`, `can_parse_prototype_with_embed`, `can_parse_context_included_in_prototype_via_named_reference`, `can_parse_with_prototype_set`, `can_parse_issue176`, `can_parse_two_with_prototypes_at_same_stack_level`, `can_parse_two_with_prototypes_at_same_stack_level_set_multiple`, `can_parse_two_with_prototypes_at_same_stack_level_updated_captures`, `can_parse_two_with_prototypes_at_same_stack_level_updated_captures_ignore_unexisting`, `can_parse_syntax_with_eol_and_newline`, `can_parse_syntax_with_eol_only`, `can_parse_syntax_with_beginning_of_line`, `can_parse_syntax_with_comment_and_eol`, `can_parse_text_with_unicode_to_skip`, `can_include_backrefs`, `can_include_nested_backrefs`, `can_avoid_infinite_stack_depth`, `expect_scope_stacks`, `expect_scope_stacks_with_syntax`, `expect_scope_stacks_for_ops`, `parse`, `link`, `ops`, `stack_states`
- **Types:** 1/5 matched (target 2)
- **Missing types:** `ParsingError`, `StateLevel`, `RegexMatch`, `SearchCache`
- **Tests:** 0/48 matched
- **Lint issues:** 1

### 9. parsing.yaml_load

- **Target:** `parsing.YamlLoad [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 424310.0
- **Functions:** 0/36 matched (target 0)
- **Missing functions:** `get_key`, `str_to_scopes`, `load_from_str`, `parse_top_level`, `parse_contexts`, `parse_context`, `parse_reference`, `parse_match_pattern`, `parse_pushargs`, `parse_regex`, `resolve_variables`, `try_compile_regex`, `parse_captures`, `add_initial_contexts`, `new`, `next`, `replace_posix_char_classes`, `regex_for_newlines`, `rewrite`, `regex_for_no_newlines`, `get_consuming_capture_indexes`, `peek`, `parse_character_class`, `can_parse`, `can_parse_embed_as_with_prototypes`, `errors_on_embed_without_escape`, `errors_on_regex_compile_error`, `can_parse_ugly_yaml`, `names_anonymous_contexts`, `can_use_fallback_name`, `can_rewrite_regex_for_newlines`, `can_rewrite_regex_for_no_newlines`, `can_get_valid_captures_from_regex`, `can_get_valid_captures_from_regex2`, `can_get_valid_captures_from_nested_regex`, `error_loading_syntax_with_unescaped_backslash`
- **Types:** 1/7 matched (target 1)
- **Missing types:** `ParserState`, `ContextNamer`, `RegexRewriterForNewlines`, `RegexRewriterForNoNewlines`, `ConsumingCaptureIndexParser`, `Parser`
- **Tests:** 0/13 matched

### 10. parsing.metadata

- **Target:** `parsing.Metadata`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 313709.4
- **Functions:** 2/26 matched (target 3)
- **Missing functions:** `add_raw`, `quick_load`, `from`, `append_vars`, `merged_with_raw`, `from_raw`, `get_line_comment_marker`, `get_block_comment_markers`, `unindented_line`, `decrease_indent`, `increase_indent`, `bracket_increase`, `disable_indent_next_line`, `line_comment`, `block_comment`, `best_match`, `load`, `serialize`, `deserialize`, `load_raw`, `load_groups`, `parse_yaml_meta`, `load_shell_vars`, `indent_rust`
- **Types:** 4/11 matched (target 6)
- **Missing types:** `Dict`, `SelectorString`, `RawMetadataEntry`, `LoadMetadata`, `KeyPair`, `ShellVars`, `MetaSetSerializable`
- **Tests:** 0/6 matched

### 11. highlighting.style

- **Target:** `highlighting.Style`
- **Similarity:** 0.21
- **Dependents:** 0
- **Priority Score:** 223807.9
- **Functions:** 12/33 matched (target 19)
- **Missing functions:** `fmt`, `bits`, `from_bits`, `from_bits_truncate`, `from_bits_unchecked`, `intersects`, `insert`, `remove`, `toggle`, `set`, `bitor`, `bitor_assign`, `bitxor`, `bitxor_assign`, `bitand`, `bitand_assign`, `sub`, `sub_assign`, `not`, `extend`, `from_iter`
- **Types:** 4/5 matched (target 6)
- **Missing types:** `Output`

### 12. dumps

- **Target:** `syntect.Dumps [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 171710.0
- **Functions:** 0/17 matched (target 0)
- **Missing functions:** `dump_to_writer`, `dump_binary`, `dump_to_file`, `from_reader`, `from_binary`, `from_dump_file`, `dump_to_uncompressed_file`, `from_uncompressed_dump_file`, `from_uncompressed_data`, `serialize_to_writer_impl`, `deserialize_from_reader_impl`, `load_defaults_nonewlines`, `load_defaults_newlines`, `load_defaults`, `can_dump_and_load`, `dump_is_deterministic`, `has_default_themes`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 0/3 matched

### 13. html

- **Target:** `html.Html`
- **Similarity:** 0.32
- **Dependents:** 0
- **Priority Score:** 143106.8
- **Functions:** 14/28 matched (target 19)
- **Missing functions:** `new`, `parse_html_for_line`, `css_for_theme`, `highlighted_html_for_file`, `tokens_to_classed_spans`, `tokens_to_classed_html`, `tokens`, `strings`, `tricky_test_syntax`, `test_classed_html_generator_doesnt_panic`, `test_classed_html_generator_prefixed`, `test_classed_html_generator_no_empty_span`, `test_escape_css_identifier`, `test_css_for_theme_with_class_style_issue_308`
- **Types:** 3/3 matched (target 11)
- **Missing types:** _none_
- **Tests:** 1/9 matched

### 14. easy

- **Target:** `easy.HighlightLines`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 91607.3
- **Functions:** 4/11 matched (target 10)
- **Missing functions:** `new`, `highlight`, `can_highlight_lines`, `can_highlight_file`, `can_find_regions`, `can_find_regions_with_trailing_newline`, `can_start_again_from_previous_state`
- **Types:** 3/5 matched (target 7)
- **Missing types:** `HighlightFile`, `Item`
- **Tests:** 0/5 matched

### 15. util

- **Target:** `util.Util`
- **Similarity:** 0.35
- **Dependents:** 0
- **Priority Score:** 91506.5
- **Functions:** 6/13 matched (target 10)
- **Missing functions:** `as_latex_escaped`, `textcolor`, `debug_print_ops`, `from`, `next`, `lines`, `test_split_at`
- **Types:** 0/2 matched
- **Missing types:** `LinesWithEndings`, `Item`
- **Tests:** 2/4 matched

### 16. highlighting.highlighter

- **Target:** `highlighting.Highlighter`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 62105.5
- **Functions:** 10/15 matched (target 16)
- **Missing functions:** `update_scored`, `can_parse`, `can_parse_with_highlight_state_from_cache`, `tricky_cases`, `test_ranges`
- **Types:** 5/6 matched (target 11)
- **Missing types:** `Item`
- **Tests:** 0/4 matched

### 17. highlighting.selector

- **Target:** `highlighting.Selector`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 61107.0
- **Functions:** 3/8 matched (target 9)
- **Missing functions:** `from_str`, `selectors_work`, `matching_works`, `empty_stack_matching_works`, `multiple_excludes_matching_works`
- **Types:** 2/3 matched
- **Missing types:** `Err`
- **Tests:** 0/4 matched

### 18. highlighting.theme_load

- **Target:** `highlighting.ThemeLoad [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 40510.0
- **Functions:** 0/2 matched (target 0)
- **Missing functions:** `from_str`, `parse_settings`
- **Types:** 1/3 matched (target 1)
- **Missing types:** `Err`, `Error`

### 19. highlighting.settings

- **Target:** `highlighting.Settings [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 30410.0
- **Functions:** 0/2 matched (target 0)
- **Missing functions:** `from`, `read_plist`
- **Types:** 1/2 matched (target 1)
- **Missing types:** `ParseSettings`

### 20. utils

- **Target:** `util.Utils [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10110.0
- **Functions:** 0/1 matched (target 0)
- **Missing functions:** `walk_dir`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 21. lib

- **Target:** `syntect.Mod`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 200.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 16)
- **Missing types:** _none_

### 22. parsing.mod

- **Target:** `parsing.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 23. highlighting.mod

- **Target:** `highlighting.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

