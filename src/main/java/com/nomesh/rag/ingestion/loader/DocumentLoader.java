package com.nomesh.rag.ingestion.loader;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Loads supported enterprise document formats into Spring AI documents.
 *
 * <p>The commercial MVP supports TXT, PDF, and DOCX documents. Format-specific
 * readers are selected explicitly so extraction behavior remains predictable
 * across document types.</p>
 *
 * @author Nomesh De Silva
 */
@Component
public class DocumentLoader {

    /**
     * Loads readable content from the supplied resource.
     *
     * @param resource uploaded document resource
     * @param sourceFileName original uploaded file name
     * @return extracted documents
     */
    public List<Document> load(
            Resource resource,
            String sourceFileName
    ) {

        String extension =
                resolveExtension(sourceFileName);

        return switch (extension) {

            case "txt" ->
                    loadText(resource);

            case "pdf" ->
                    loadPdf(resource);

            case "docx" ->
                    loadDocx(
                            resource,
                            sourceFileName
                    );

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported document type: "
                                    + extension
                                    + ". Supported types are TXT, PDF and DOCX."
                    );
        };
    }

    private List<Document> loadText(Resource resource) {

        return new TextReader(resource)
                .get();
    }

    private List<Document> loadPdf(Resource resource) {

        return new TikaDocumentReader(resource)
                .get();
    }

    private List<Document> loadDocx(
            Resource resource,
            String sourceFileName
    ) {

        try (
                InputStream inputStream =
                        resource.getInputStream();

                XWPFDocument wordDocument =
                        new XWPFDocument(inputStream);

                XWPFWordExtractor extractor =
                        new XWPFWordExtractor(wordDocument)
        ) {

            String text =
                    extractor.getText();

            if (text == null || text.isBlank()) {
                return List.of();
            }

            return List.of(
                    new Document(
                            text.trim(),
                            Map.of(
                                    "source",
                                    sourceFileName
                            )
                    )
            );

        } catch (IOException exception) {

            throw new IllegalArgumentException(
                    "Unable to read DOCX document: "
                            + sourceFileName,
                    exception
            );
        }
    }

    private String resolveExtension(
            String sourceFileName
    ) {

        if (sourceFileName == null
                || sourceFileName.isBlank()) {

            throw new IllegalArgumentException(
                    "Source file name is required."
            );
        }

        int separatorIndex =
                sourceFileName.lastIndexOf('.');

        if (separatorIndex < 0
                || separatorIndex
                == sourceFileName.length() - 1) {

            throw new IllegalArgumentException(
                    "Uploaded document must contain a valid file extension."
            );
        }

        return sourceFileName
                .substring(separatorIndex + 1)
                .toLowerCase(Locale.ROOT);
    }
}