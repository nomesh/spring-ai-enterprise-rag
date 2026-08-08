package com.nomesh.rag.ingestion.splitter;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Splits extracted document text into bounded chunks suitable for embedding.
 *
 * <p>The splitter uses deterministic character boundaries rather than a
 * tokenizer-specific implementation so ingestion behaves consistently across
 * TXT, PDF, and DOCX documents.</p>
 *
 * @author Nomesh De Silva
 */
@Component
public class DocumentChunker {

    private static final int CHUNK_SIZE = 2000;
    private static final int CHUNK_OVERLAP = 200;

    /**
     * Splits extracted documents into bounded text chunks while preserving
     * source metadata.
     *
     * @param documents extracted source documents
     * @return chunked documents
     */
    public List<Document> split(List<Document> documents) {

        if (documents == null || documents.isEmpty()) {
            return List.of();
        }

        List<Document> chunks = new ArrayList<>();

        for (Document document : documents) {

            String text = document.getText();

            if (text == null || text.isBlank()) {
                continue;
            }

            chunks.addAll(
                    splitDocument(
                            text,
                            document.getMetadata()
                    )
            );
        }

        return chunks;
    }

    private List<Document> splitDocument(
            String text,
            Map<String, Object> metadata
    ) {

        List<Document> chunks = new ArrayList<>();

        int start = 0;

        while (start < text.length()) {

            int desiredEnd =
                    Math.min(
                            start + CHUNK_SIZE,
                            text.length()
                    );

            int end =
                    findNaturalBoundary(
                            text,
                            start,
                            desiredEnd
                    );

            String chunkText =
                    text.substring(start, end)
                            .trim();

            if (!chunkText.isBlank()) {
                chunks.add(
                        new Document(
                                chunkText,
                                metadata
                        )
                );
            }

            if (end >= text.length()) {
                break;
            }

            int nextStart =
                    Math.max(
                            end - CHUNK_OVERLAP,
                            start + 1
                    );

            start = nextStart;
        }

        return chunks;
    }

    private int findNaturalBoundary(
            String text,
            int start,
            int desiredEnd
    ) {

        if (desiredEnd >= text.length()) {
            return text.length();
        }

        int minimumBoundary =
                Math.min(
                        start + (CHUNK_SIZE / 2),
                        desiredEnd
                );

        int paragraphBoundary =
                text.lastIndexOf(
                        "\n\n",
                        desiredEnd
                );

        if (paragraphBoundary >= minimumBoundary) {
            return paragraphBoundary + 2;
        }

        int sentenceBoundary =
                text.lastIndexOf(
                        ". ",
                        desiredEnd
                );

        if (sentenceBoundary >= minimumBoundary) {
            return sentenceBoundary + 1;
        }

        int whitespaceBoundary =
                text.lastIndexOf(
                        ' ',
                        desiredEnd
                );

        if (whitespaceBoundary >= minimumBoundary) {
            return whitespaceBoundary;
        }

        return desiredEnd;
    }
}