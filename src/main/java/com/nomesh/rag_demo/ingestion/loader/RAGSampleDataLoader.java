package com.nomesh.rag_demo.ingestion.loader;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RAGSampleDataLoader {
    private final VectorStore vectorStore;

    public RAGSampleDataLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public VectorStore getVectorStore() {
        return vectorStore;
    }

    @PostConstruct
    public void loadDataToVectorStore() {
        List<String> sampleData = getSampleData();

        // chunks -> embedding --> vector store
        List<Document> documents = sampleData.stream()
                .map(Document::new).toList();

        vectorStore.add(documents);

    }

    private List<String> getSampleData(){
        return List.of("Science\n" +
                "CRISPR gene editing allows precise DNA modifications using a bacterial immune system. The Cas9 enzyme acts like molecular scissors, guided by RNA to target specific genetic sequences. This technology enables correcting disease-causing mutations, creating disease models, and developing gene therapies. Recent advances include base editing and prime editing, which allow single-letter DNA changes without double-strand breaks, reducing unintended mutations.\n" +
                "Astronomy\n" +
                "The James Webb Space Telescope observes infrared light from distant galaxies, stars, and exoplanets. Its 6.5-meter gold-coated beryllium mirror and sunshield allow unprecedented sensitivity. JWST has detected early galaxies formed just 300 million years after the Big Bang, analyzed exoplanet atmospheres for water and methane, and revealed star formation in nebulae. Unlike Hubble, it orbits at L2, a gravitationally stable point 1.5 million km from Earth.\n" +
                "Aikido\n" +
                "Aikido is a Japanese martial art emphasizing harmony and non-resistance. Practitioners use circular movements to redirect an attacker's energy, applying joint locks and throws without opposing force. Founded by Morihei Ueshiba, it incorporates philosophical principles of peace and reconciliation. Training includes empty-hand techniques, weapons practice with bokken and jo, and paired exercises called kata. The art focuses on blending with aggression rather than meeting it with counter-force.");
    }


}
