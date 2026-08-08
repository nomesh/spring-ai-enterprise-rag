
---

### 2. REPLACE `docs/architecture/enterprise-search.md`

Replace the entire old Sprint 4 version with this concise current version:

```markdown
# Enterprise Search Architecture

## Overview

Enterprise Search provides framework-independent semantic retrieval, metadata filtering, validation, and bounded result pagination backed by Spring AI and PGVector.

The architecture separates the public Enterprise RAG Platform contract from retrieval infrastructure.

## Search Pipeline

```text
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
  +-----------------------------+
  |                             |
  v                             v
SearchPaginationResolver   DocumentSearchFilter
  |                             |
  v                             v
SearchPagination        MetadataFilterTranslator
  |                             |
  v                             |
SearchWindowResolver            |
  |                             |
  v                             |
SearchWindow                    |
  |                             |
  +-------------+---------------+
                |
                v
         DocumentRetriever
                |
                v
      Spring AI SearchRequest
                |
                v
             PGVector
                |
                v
        Ranked Candidates
                |
                v
         Result Window
                |
                v
       SearchResultMapper
                |
                v
     EnterpriseSearchResponse
                |
                v
              Client