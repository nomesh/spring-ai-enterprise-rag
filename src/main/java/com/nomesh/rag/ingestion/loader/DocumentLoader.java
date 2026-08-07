package com.nomesh.rag.ingestion.loader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentLoader {
    public List<Document> load(Resource resource) {

        TextReader reader = new TextReader(resource);
        return reader.get();
    }
}
