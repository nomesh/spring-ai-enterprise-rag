# Enterprise Search Architecture

## Overview

Enterprise Search provides semantic retrieval combined with metadata filtering while keeping the platform independent from the underlying AI framework and vector database.

The architecture introduced in Sprint 4 establishes the foundation for future search capabilities such as hybrid search, faceted search, reranking, pagination, and multi-tenancy.

## Goals

The Enterprise Search architecture is designed to:

* support semantic search
* support metadata-filtered retrieval
* expose a stable enterprise-facing API
* prevent Spring AI implementation details from leaking into public contracts
* allow future replacement of PGVector or Spring AI
* centralize request validation
* keep search responsibilities separated
* provide a foundation for future search-engine capabilities

## High-Level Architecture

```text
                        Enterprise Search Pipeline

Client
  |
  v
POST /api/v1/search
  |
  v
EnterpriseSearchController
  |
  v
EnterpriseSearchRequestValidator
  |
  v
EnterpriseSearchRequest
  |
  +------------------------------+
  |                              |
  v                              v
DocumentRetriever        DocumentSearchFilter
                                 |
                                 v
                       MetadataFilterTranslator
                                 |
                                 v
                       Spring AI Filter.Expression
                                 |
                                 +------------+
                                              |
                                              v
                                  Spring AI SearchRequest
                                              |
                                              v
                                           PGVector
                                              |
                                              v
                                    List<Document>
                                              |
                                              v
                                      SearchResultMapper
                                              |
                                              v
                                  EnterpriseSearchResponse
                                              |
                                              v
                                            Client
```

## Core Components

### EnterpriseSearchRequest

Represents the platform-level search request.

It contains:

* query text
* top-K limit
* similarity threshold
* optional metadata filters

The model is independent from Spring AI.

### DocumentSearchFilter

Represents metadata constraints applied to semantic search.

Currently supported fields include:

* department
* file type
* source
* author
* classification
* tenant ID

Only supplied values participate in filtering.

### EnterpriseSearchRequestValidator

Validates search requests before retrieval execution.

Current validation rules include:

* request must not be null
* query must not be blank
* `topK` must be greater than zero when provided
* `topK` must not exceed the configured maximum
* similarity threshold must be between `0.0` and `1.0`

Validation is centralized to prevent duplication across API endpoints and retrieval components.

### MetadataFilterTranslator

Converts `DocumentSearchFilter` into Spring AI metadata filter expressions.

This class forms an infrastructure adapter between the platform-level search contract and Spring AI.

Multiple metadata filters are combined using logical `AND`.

If no meaningful filters are provided, no filter expression is generated.

### DocumentRetriever

Coordinates semantic retrieval.

Responsibilities include:

* applying configured defaults
* applying request-specific overrides
* translating metadata filters
* building Spring AI `SearchRequest`
* invoking the configured `VectorStore`

The retriever does not define the public API contract.

### SearchResultMapper

Converts Spring AI `Document` instances into framework-independent `SearchResult` models.

This prevents infrastructure-specific response types from escaping into the REST API.

### EnterpriseSearchResponse

Represents the public search response.

The response contains:

* original query
* total number of returned results
* framework-independent search results

## Search Request Example

```json
{
  "query": "annual leave",
  "topK": 10,
  "similarityThreshold": 0.5,
  "filter": {
    "source": "hr-leave-policy.txt"
  }
}
```

## Search Response Example

```json
{
  "query": "annual leave",
  "totalResults": 1,
  "results": [
    {
      "content": "Employees are entitled to 20 days of annual leave each year.",
      "score": 0.8121,
      "metadata": {
        "source": "hr-leave-policy.txt",
        "documentName": "hr-leave-policy.txt",
        "fileType": "txt"
      }
    }
  ]
}
```

## Metadata Filtering Flow

Metadata-filtered search executes semantic similarity and metadata constraints together.

For example:

```text
Semantic Query
"annual leave"

AND

Metadata Constraint
source = "hr-leave-policy.txt"
```

The vector database therefore returns only documents that satisfy the metadata condition and are semantically relevant.

This prevents application-side filtering after retrieval and allows the search engine to reduce the candidate set before returning results.

## Error Handling

Invalid search requests are rejected before retrieval.

Example:

```json
{
  "query": "",
  "topK": 10
}
```

Response:

```json
{
  "error": "INVALID_SEARCH_REQUEST",
  "message": "Search query must not be blank."
}
```

HTTP status:

```text
400 Bad Request
```

API exception translation is handled centrally using the global exception handler.

## Framework Boundary

The following types belong to the Enterprise RAG Platform contract:

```text
EnterpriseSearchRequest
DocumentSearchFilter
EnterpriseSearchResponse
SearchResult
```

The following types are infrastructure implementation details:

```text
org.springframework.ai.document.Document
org.springframework.ai.vectorstore.SearchRequest
org.springframework.ai.vectorstore.filter.Filter.Expression
VectorStore
PGVector
```

Infrastructure-specific types must not become part of the public API contract.

## Current Limitations

Sprint 4 intentionally does not implement:

* OR / NOT filter operators
* range filters
* pagination
* sorting
* facets
* keyword or BM25 search
* hybrid search
* reranking
* result highlighting
* multi-index search
* access-control enforcement

These capabilities will be introduced only when required by future search stories.

## Testing Strategy

The current search architecture is covered by:

* metadata validation unit tests
* metadata mapper unit tests
* metadata filter translator unit tests
* document retriever unit tests
* enterprise search request validator unit tests
* Spring MVC integration tests for API validation behavior
* manual end-to-end verification against PGVector

A fully isolated PGVector integration-test environment using Testcontainers is planned as a hardening improvement.

## Future Evolution

The architecture is designed to evolve toward capabilities commonly found in enterprise search platforms such as Elasticsearch, OpenSearch, and Azure AI Search.

Potential future additions include:

```text
EnterpriseSearchRequest
        |
        v
Search Planner
   /          \
  v            v
Vector Search  Keyword Search
   \          /
    v        v
     Hybrid Ranking
          |
          v
       Reranker
          |
          v
       SearchResult
```

The public search contract should remain stable while these capabilities evolve behind the application boundary.

## Related ADRs

* ADR-008: Enterprise Document Metadata Model
* ADR-009: Enterprise Search Abstraction

## Sprint 4 Outcome

Sprint 4 established the platform's first production-oriented enterprise search capability:

* semantic retrieval
* metadata filtering
* framework-independent request models
* framework-independent response models
* centralized validation
* REST API
* test coverage
* PGVector-backed filtered search

This architecture forms the foundation for the next generation of enterprise search features.
