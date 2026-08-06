# Enterprise RAG Platform - AI Context

## Project Overview

This project is an Enterprise Retrieval Augmented Generation (RAG)
platform built using Spring AI.

The goal is to build a production-grade RAG solution supporting:
- Enterprise document ingestion
- Semantic search
- Hybrid search
- Source citations
- Security
- Multi-tenancy
- AI agents
- Observability


## Technology Stack

Backend:
- Java 17
- Spring Boot 4.0.7
- Spring AI 2.0.0
- Maven

LLM:
- Ollama
- qwen2.5:7b

Embedding:
- Ollama
- nomic-embed-text

Vector Database:
- Qdrant

Database:
- H2 (development)
- PostgreSQL (planned)


## Current Implementation Status

Completed:

[x] Basic RAG pipeline

Implemented:
- Document loading
- Text splitting
- Embedding generation
- Vector storage
- Similarity search
- Prompt augmentation
- LLM response generation


Current Branch:

feature/rag-source-citations


Current Feature:

Adding source references to generated answers.


## Current Architecture

Flow:

Documents
|
Document Loader
|
Text Splitter
|
Embedding Model
|
Qdrant Vector Store
|
Similarity Search
|
Retrieved Documents
|
Prompt Builder
|
Ollama qwen2.5:7b
|
Response


## Coding Conventions

Use:
- Spring Boot best practices
- Constructor injection
- Clean architecture
- DTO based API responses
- Feature branch workflow


## Git Strategy

Branches:

master
|
development
|
feature/*


Completed features merge:

feature branch
|
development
|
master


## Current Task

Implement RAG Source Citations.

Requirements:

1. Preserve document metadata during ingestion.
2. Retrieve metadata with similarity search.
3. Return citations with answers.

Expected response:

{
"answer":"...",
"sources":[
{
"document":"sample.pdf",
"page":2
}
]
}


## Important Decisions

- Use Ollama for local LLM inference.
- Avoid cloud APIs initially.
- Build enterprise architecture gradually.
- Prioritize production patterns over tutorials.