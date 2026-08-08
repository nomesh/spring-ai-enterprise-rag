package com.nomesh.rag.ingestion.loader;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentLoaderTest {

    private final DocumentLoader loader =
            new DocumentLoader();

    @Test
    void shouldLoadTxtDocument() {

        ByteArrayResource resource =
                new ByteArrayResource(
                        "Annual leave policy for employees."
                                .getBytes(StandardCharsets.UTF_8)
                );

        List<Document> documents =
                loader.load(
                        resource,
                        "hr-policy.txt"
                );

        assertFalse(documents.isEmpty());

        assertTrue(
                documents.stream()
                        .anyMatch(document ->
                                document.getText()
                                        .contains("Annual leave policy")
                        )
        );
    }

    @Test
    void shouldLoadDocxDocument() throws Exception {

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        try (XWPFDocument document =
                     new XWPFDocument()) {

            XWPFParagraph paragraph =
                    document.createParagraph();

            paragraph.createRun()
                    .setText(
                            "Employees are entitled to annual leave."
                    );

            document.write(outputStream);
        }

        ByteArrayResource resource =
                new ByteArrayResource(
                        outputStream.toByteArray()
                );

        List<Document> documents =
                loader.load(
                        resource,
                        "employee-handbook.docx"
                );

        assertFalse(documents.isEmpty());

        assertTrue(
                documents.stream()
                        .anyMatch(document ->
                                document.getText()
                                        .contains("annual leave")
                        )
        );
    }

    @Test
    void shouldRejectUnsupportedDocumentType() {

        ByteArrayResource resource =
                new ByteArrayResource(
                        "unsupported".getBytes(StandardCharsets.UTF_8)
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> loader.load(
                        resource,
                        "data.csv"
                )
        );
    }
}