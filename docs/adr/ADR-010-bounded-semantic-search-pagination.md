# ADR-010: Bounded Semantic Search Pagination

## Status

Accepted

## Date

2026-08-08

## Context

Enterprise Search requires pagination without coupling the public API to Spring Data, PGVector, or another retrieval engine.

Semantic vector retrieval differs from traditional relational pagination. A vector search typically retrieves the top-K ranked candidates rather than executing a stable offset-based query across a complete result set.

Exposing vector-specific retrieval concepts directly to API consumers would weaken the framework-independent search contract established by ADR-009.

The platform therefore requires pagination semantics that:

- remain independent from the underlying search engine
- prevent unbounded candidate retrieval
- support a `hasMore` indicator
- avoid claiming an exact total result count when one is not available
- remain compatible with future hybrid search and reranking

## Decision

The public search API will expose pagination through `SearchPagination`.

Pagination contains:

- `page` — zero-based requested page
- `size` — requested page size

Default values are:

- page: `0`
- size: `20`

Maximum page size is `100`.

Pagination defaults are resolved by `SearchPaginationResolver`.

The application calculates an internal `SearchWindow` containing:

- result offset
- page size
- retrieval limit

The retrieval limit is calculated as:

```text
offset = page * size

retrievalLimit = offset + size + 1