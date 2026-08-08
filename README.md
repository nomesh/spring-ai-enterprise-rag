
---

### 3. CREATE root `README.md`

This one matters commercially.

```markdown
# Enterprise RAG Platform

Enterprise RAG Platform is an enterprise-oriented Retrieval-Augmented Generation and knowledge-search platform designed for organizations that need secure, structured, AI-powered access to internal knowledge.

The platform combines document ingestion, semantic retrieval, metadata-aware enterprise search, vector storage, and RAG capabilities behind framework-independent application contracts.

## Current Release

**MVP 0.1.0**

## Core Capabilities

### Enterprise Knowledge Ingestion

- dynamic document upload
- persistent document storage
- document chunking
- embedding generation
- PGVector indexing
- document listing and deletion
- vector cleanup

### Enterprise Metadata

Documents are enriched using a canonical metadata model supporting enterprise concepts such as:

- source
- document name
- file type
- author
- department
- classification
- tenant identifier

### Enterprise Search

The search API supports:

- semantic vector search
- similarity thresholds
- metadata-filtered retrieval
- bounded pagination
- source metadata
- framework-independent request and response models

Example:

```http
POST /api/v1/search
Content-Type: application/json