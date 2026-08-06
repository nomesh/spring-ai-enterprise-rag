package com.nomesh.rag_demo.utils;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

public class RAGutils {

    /**
     * Retrieve the documents (fetch) via a query.
     * @param prompt
     * @return
     */
    public static SearchRequest Retrieve(String prompt){
        SearchRequest searchRequest = new SearchRequest().builder()
                .query(prompt)
                .topK(3) // retrieve top3 relevant documents from vector store
                .similarityThreshold(0.5) // search for documents with similarity search
                .build();

        return searchRequest;
    }

    /**
     * Augmenting the similarDocuments retrieved from the Search Request
     * @param vectorStore
     * @param searchRequest
     * @return
     */
    public static List<String> Augment(VectorStore vectorStore, SearchRequest searchRequest){
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

        // extract the text content from the retrieved documents
        List<String> similarSearchResult = similarDocuments.stream()
                .map(Document::getText)
                .toList();

        return similarSearchResult;
    }

    /**
     * Generate - Handover the context to the LLM
     * @param chatClient
     * @param template
     * @param similarSearchResult
     * @param prompt
     * @param username
     * @return
     */
    public static String Generate(ChatClient chatClient, Resource template, List<String> similarSearchResult, String prompt, String username ){
        String results = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec
                        .text(template)
                        .param("documents",similarSearchResult))
                .advisors(adviceSpec -> adviceSpec.param(CONVERSATION_ID, username))
                .user(prompt)
                .call()
                .content();


        return results;
    }

    /**
     * Prepearing the MetaData for the Citation
     * @param metadata
     * @param key
     * @param type
     * @return
     * @param <T>
     */
    public static <T> T getMetadata(
            Map<String, Object> metadata,
            String key,
            Class<T> type) {

        Object value = metadata.get(key);

        if (value == null) {
            return null;
        }

        try {
            if (type == String.class) {
                return type.cast(value.toString());
            }

            if (type == Integer.class) {
                return type.cast(Integer.valueOf(value.toString()));
            }

            if (type == Double.class) {
                return type.cast(Double.valueOf(value.toString()));
            }

            return type.cast(value);

        } catch (Exception ex) {
            return null;
        }
    }

}
