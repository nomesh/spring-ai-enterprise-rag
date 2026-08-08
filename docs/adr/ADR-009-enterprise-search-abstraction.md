# ADR-009: Enterprise Search Abstraction

## Status

Accepted

## Date

2026-08-08

## Context

The Enterprise RAG Platform has evolved from basic semantic retrieval into metadata-filtered enterprise search.

The search flow now includes:

* `EnterpriseSearchRequest`
* `DocumentSearchFilter`
* `EnterpriseSearchRequestValidator`
* `MetadataFilterTranslator`
* `DocumentRetriever`
* `SearchResultMapper`
* `EnterpriseSearchResponse`

Spring AI is currently used to execute vector search through PGVector.

However, the platform is intended to remain independent from any single AI framework or vector-search implementation.

Allowing Spring AI types such as `Document`, `SearchRequest`, or `Filter.Expression` to become part of the public API or domain contract would create unnecessary framework coupling and make future migration significantly more difficult.

Potential future implementations may include:

* LangChain4J
* Elasticsearch
* OpenSearch
* Azure AI Search
* Qdrant
* Milvus
* Pinecone
* other vector or hybrid search technologies

The platform therefore requires a stable enterprise search contract that is independent from its current search infrastructure.

## Decision

The Enterprise RAG Platform will expose its own framework-independent search models.

The public search contract will use:

* `EnterpriseSearchRequest`
* `DocumentSearchFilter`
* `EnterpriseSearchResponse`
* `SearchResult`

Spring AI types must not be exposed through the public REST API.

Infrastructure-specific translation will occur behind the platform boundary.

The current search architecture is:

```text
Client
  |
  v
EnterpriseSearchController
  |
  v
EnterpriseSearchRequestValidator
  |
  v
DocumentRetriever
  |
  +--> MetadataFilterTranslator
  |
  v
Spring AI SearchRequest
  |
  v
PGVector
  |
  v
Spring AI Document
  |
  v
SearchResultMapper
  |
  v
EnterpriseSearchResponse
```

`MetadataFilterTranslator` is responsible for translating platform-level metadata filters into Spring AI filter expressions.

`SearchResultMapper` is responsible for converting infrastructure retrieval results into platform-level search results.

`EnterpriseSearchRequestValidator` centralizes request validation before retrieval execution.

## Rationale

This approach creates a clear boundary between business-facing search concepts and infrastructure-specific implementation details.

The design provides several benefits.

### Framework independence

Consumers of the Enterprise Search API do not depend on Spring AI.

If Spring AI is replaced in the future, the public API can remain unchanged.

### Infrastructure replaceability

PGVector is treated as an implementation detail rather than part of the platform contract.

Future search backends can be introduced behind the same platform-level abstractions.

### Stable API contract

Clients interact with search concepts defined by the Enterprise RAG Platform rather than third-party framework models.

### Testability

Validation, filtering, mapping, and retrieval orchestration can be tested independently.

### Separation of concerns

Each component has a focused responsibility:

* Controller: HTTP orchestration
* Validator: request validation
* Retriever: retrieval orchestration
* Filter translator: infrastructure-specific filter translation
* Result mapper: infrastructure-to-platform response conversion

## Consequences

### Positive

* Reduced Spring AI coupling
* Easier future migration to alternative search technologies
* Cleaner public API
* Improved testability
* Better adherence to Dependency Inversion
* Clearer separation between application and infrastructure concerns

### Negative

* Additional mapping classes are required
* Platform models may duplicate some capabilities already available in Spring AI
* New search capabilities may require updates to both platform models and infrastructure adapters

These costs are considered acceptable because maintaining a stable enterprise-facing contract is more important than minimizing the number of classes.

## Alternatives Considered

### Expose Spring AI `Document` directly

Rejected.

Although simpler initially, this would permanently couple API consumers to Spring AI.

### Use Spring AI `SearchRequest` as the platform request model

Rejected.

This would leak infrastructure-specific concepts into the business and API layers.

### Build a complete generic search engine abstraction immediately

Rejected.

A large abstraction hierarchy would introduce unnecessary complexity before requirements such as hybrid search, pagination, facets, ranking, and multiple backends are fully understood.

The current design follows YAGNI while preserving extension points.

## Future Considerations

The abstraction may evolve to support:

* hybrid semantic and keyword search
* pagination
* sorting
* faceted search
* advanced filter operators
* result reranking
* collections and indexes
* multi-tenancy
* access-control filtering
* multiple vector stores
* multiple search engines

These capabilities should extend the platform-level search contract without exposing infrastructure-specific types.

## Decision Outcome

Accepted.

Enterprise Search will remain framework-independent at the public and application boundaries, while Spring AI and PGVector remain infrastructure implementation details.
