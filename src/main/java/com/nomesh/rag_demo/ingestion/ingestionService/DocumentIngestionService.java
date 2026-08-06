package com.nomesh.rag_demo.ingestion.ingestionService;

import com.nomesh.rag_demo.ingestion.indexer.DocumentIndexer;
import com.nomesh.rag_demo.ingestion.loader.DocumentLoader;
import com.nomesh.rag_demo.ingestion.processor.DocumentMetadataEnricher;
import com.nomesh.rag_demo.ingestion.splitter.DocumentChunker;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIngestionService {
    private final DocumentLoader loader;
    private final DocumentIndexer indexer;
    private final DocumentChunker chunker;
    private final DocumentMetadataEnricher metadataEnricher;


    public DocumentIngestionService(
            DocumentLoader loader,
            DocumentChunker chunker,
            DocumentMetadataEnricher metadataEnricher,
            DocumentIndexer indexer
    ) {
        this.loader = loader;
        this.chunker = chunker;
        this.metadataEnricher = metadataEnricher;
        this.indexer = indexer;
    }


    public void ingest() {

        Resource resource =
                new ClassPathResource("documents/company-policy.txt");

        // direct loading of document objects - bad practise
//        List<Document> documents =  loader.load(resource);
//        indexer.index(documents);

        //chunking  - best practise
        List<Document> documents =
                loader.load(resource);

        /*
         * chunking before ingestion
         */
        List<Document> chunks =
                chunker.split(documents);

        /*
         * Enriching the chunks for better identity adding Meta-data.
         */
        List<Document> enrichedChunks =
                metadataEnricher.enrich(
                        chunks,
                        resource.getFilename()
                );

        indexer.index(enrichedChunks);
    }
}
