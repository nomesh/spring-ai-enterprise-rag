package com.nomesh.rag_demo.service;


import com.nomesh.rag_demo.model.SourceCitation;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
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



        return new SourceCitation(

                documentName,

                documentType,

                pageNumber,

                chunkId,

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