package com.nomesh.rag.search.filter;

/**
 * Defines optional metadata filters used when searching enterprise documents.
 *
 * <p>Only provided values are applied during retrieval, allowing semantic
 * search to be narrowed by document attributes such as department,
 * file type, source, or classification.</p>
 *
 * @param department document department
 * @param fileType document type such as pdf or docx
 * @param source document origin
 * @param author document author
 * @param classification document classification
 * @param tenantId tenant that owns the document
 */
public record DocumentSearchFilter(
        String department,
        String fileType,
        String source,
        String author,
        String classification,
        String tenantId
) {
}
