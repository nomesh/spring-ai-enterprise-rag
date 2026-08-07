### Document Metadata

The `DocumentMetadata` model represents business information about
a document, such as its author, department and source.

It is created during document ingestion and remains independent
from the underlying vector database.

This allows the search layer to evolve without being tightly coupled
to a specific storage technology.

The ingestion pipeline uses a source-independent metadata model to normalize
metadata before documents are indexed.

```text
                         Document Source
                               │
              ┌────────────────┼────────────────┐
              │                │                │
          Local File       Confluence         Jira
              │                │                │
              └────────────────┼────────────────┘
                               │
                               ▼
                     Source Metadata Mapper
                               │
                               ▼
                       DocumentMetadata
                               │
                               ▼
                          Validation
                               │
                               ▼
                            Chunking
                               │
                               ▼
                    Chunk Metadata Enrichment
                               │
                               ▼
                       Spring AI Document
                               │
                               ▼
                            PGVector