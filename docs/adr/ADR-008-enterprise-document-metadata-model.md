# ADR-008: Canonical Enterprise Document Metadata Model

## Status

Accepted

## Date

2026-08-07

## Context

The platform currently stores document metadata during ingestion.
As we add enterprise features such as metadata filtering,
multi-tenancy and Confluence integration, every document source
must produce metadata in a consistent format.

Without a standard model, each connector could introduce different
metadata names, making search and maintenance difficult.

As the platform evolves toward enterprise search, metadata will also be
required for:

- metadata-based filtering;
- - Metadata can be validated consistently before documents enter the indexing pipeline.
- access-control-aware retrieval;
- multi-tenancy;
- document classification;
- enterprise connectors;
- document versioning;
- governance and auditing;
- hybrid search;
- source-specific integrations such as Confluence, Jira, SharePoint,
  and Google Drive.

Allowing individual components or connectors to independently define metadata
keys would create inconsistent schemas and tightly couple retrieval logic to
specific ingestion sources.

A stable source-independent metadata contract is therefore required.

## Decision

The platform will introduce a canonical document-level metadata model named
`DocumentMetadata`.

`DocumentMetadata` represents business metadata belonging to the logical
source document rather than metadata belonging to an individual vector chunk.

The initial canonical document metadata includes:

- documentId
- documentName
- fileType
- source
- author
- department
- tags
- language
- uploadedBy
- uploadedAt
- version
- tenantId
- classification
- status

Metadata keys used when storing metadata in Spring AI documents or the vector
store will be centralized in `MetadataConstants`.

Chunk-specific properties such as `chunkNumber` and `totalChunks` remain
separate from `DocumentMetadata` because they describe generated retrieval
units rather than the source document itself.

The canonical metadata domain model will remain independent from PGVector and
Spring AI-specific filtering APIs.

Source-specific ingestion components will be responsible for mapping their
metadata into the canonical model before indexing.

## Rationale

A source-independent metadata model prevents enterprise search capabilities
from becoming coupled to individual document providers.

For example, the search layer should be able to request:

`department = HR`

without needing to know whether the matching document originated from a local
PDF, Confluence, SharePoint, or another source.

Separating the canonical business model from vector-store representation also
allows the platform to evolve or replace the underlying vector database
without changing the metadata contract exposed to higher layers.

Keeping chunk metadata separate from document metadata maintains clear domain
boundaries between the logical document lifecycle and its indexed retrieval
representation.

## Alternatives Considered

### Continue using raw `Map<String, Object>` metadata

Rejected because string keys distributed throughout the codebase would make
schema evolution error-prone and would provide no domain-level contract.

### Couple metadata directly to PGVector

Rejected because search and ingestion domain models should not depend on a
specific persistence technology.

### Create different metadata schemas for each connector

Rejected because retrieval and filtering would need connector-specific logic
and queries.

### Store chunk information inside `DocumentMetadata`

Rejected because chunk information is generated during indexing and does not
describe the logical source document.

## Consequences

### Positive

- Consistent metadata across document sources.
- Stronger domain model.
- Easier enterprise metadata filtering.
- Reduced use of hardcoded metadata keys.
- Easier implementation of future connectors.
- Supports future multi-tenancy and security filtering.
- Vector-store technology remains isolated from the business model.
- Easier validation and automated testing.

### Negative

- Existing ingestion code must be gradually migrated to the canonical schema.
- Changes to persisted metadata keys may require backward compatibility or
  migration strategies once production data exists.
- Additional mapping is required between domain metadata and Spring AI
  document metadata.

## Future Considerations

The following capabilities are intentionally not implemented by this ADR:

- tenant authorization enforcement;
- document-level ACLs;
- metadata schema versioning;
- metadata extraction using LLMs;
- custom metadata fields;
- connector-specific extension metadata;
- document version history;
- retention policies.

These capabilities will be introduced through separate architectural
decisions when required.

## Decision Outcome

Accepted.

All new document ingestion capabilities should use the canonical
`DocumentMetadata` model and `MetadataConstants` rather than creating
independent metadata schemas.