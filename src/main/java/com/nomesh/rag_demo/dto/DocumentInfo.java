package com.nomesh.rag_demo.dto;

/**
 * This is not Spring AI's Document.
 * This is our API model.
 * @param id
 * @param fileName
 * @param fileType
 * @param uploadedAt
 * @param status
 *
 * @author Nomesh De Silva
 */
public record DocumentInfo(

        String id,

        String fileName,

        String fileType,

        String uploadedAt,

        String status

) {
}