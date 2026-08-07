package com.nomesh.rag.service;


import com.nomesh.rag.model.MetaDataKeys;
import com.nomesh.rag.model.SourceCitation;
import com.nomesh.rag.utils.RAGutils;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class CitationService {


    public List<SourceCitation> createCitations(
            List<Document> documents
    ) {


        return documents.stream()
                .map(this::convert)
                .collect(Collectors.toList());

    }


    private SourceCitation convert(
            Document document
    ) {


        String documentName =
                getMetadata(
                        document,
                        "source"
                );


        String documentType =
                getMetadata(
                        document,
                        "type"
                );


        String pageNumber =
                getMetadata(
                        document,
                        "page_number"
                );


        String chunkId =
                getMetadata(
                        document,
                        "chunk_number"
                );


        Double score =
                document.getMetadata()
                        .containsKey("distance")
                        ?
                        Double.valueOf(
                                document.getMetadata()
                                        .get("distance")
                                        .toString()
                        )
                        :
                        null;


        Map<String, Object> metadata = document.getMetadata();

        return new SourceCitation(

                RAGutils.getMetadata(
                        metadata,
                        documentName,
                        String.class
                ),
                RAGutils.getMetadata(
                        metadata,
                        documentType,
                        String.class
                ),
                RAGutils.getMetadata(
                        metadata,
                        MetaDataKeys.PAGE_NUMBER,
                        Integer.class
                ),
                RAGutils.getMetadata(
                        metadata,
                        MetaDataKeys.CHUNK_NUMBER,
                        Integer.class
                ),

                score
        );
    }



    private String getMetadata(
            Document document,
            String key
    ){

        Object value =
                document.getMetadata()
                        .get(key);


        return value != null
                ?
                value.toString()
                :
                null;

    }

}