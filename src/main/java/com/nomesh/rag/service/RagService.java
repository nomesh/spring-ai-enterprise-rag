package com.nomesh.rag.service;

import com.nomesh.rag.model.MetaDataKeys;
import com.nomesh.rag.model.RAGResponse;
import com.nomesh.rag.model.SourceCitation;
import com.nomesh.rag.retrieval.DocumentRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nomesh.rag.utils.RAGutils.*;

@Service
public class RagService {


    private final ChatClient chatClient;
    private final DocumentRetriever documentRetriever;

    public RagService(
            ChatClient.Builder builder,
            DocumentRetriever documentRetriever
    ) {


        ChatMemory chatMemory =
                MessageWindowChatMemory
                        .builder()
                        .chatMemoryRepository(
                                new InMemoryChatMemoryRepository()
                        )
                        .maxMessages(10)
                        .build();


        this.chatClient =
                builder
                        .defaultAdvisors(
                                MessageChatMemoryAdvisor
                                        .builder(chatMemory)
                                        .build()
                        )
                        .build();


        this.documentRetriever = documentRetriever;
    }


    public RAGResponse ask(
            String conversationId,
            String message
    ) {


        List<Document> documents = documentRetriever.retrieve(message);

        // No documents found from semantic search
        if (documents.isEmpty()) {
            return new RAGResponse("I could not find relevant information in the company knowledge base.", List.of());
        }

        /*
            Create the CONTEXT out of retrieved documents
         */

        String context =
                documents
                .stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        /* PROMPT :
                ask to perform a similarity (semantic) search for the context generated above only.
         */

        String prompt = """
        You are an enterprise knowledge assistant.

        Use only the provided context to answer the question.

        Extract the answer from the context even if
        the wording is different from the question.

        Do not say you don't know if the answer
        can reasonably be inferred from the context.
        
        If the answer is not available in the context,
        state that you do not have enough information.
        Do not make up information.
        
        Answer concisely.
        Return only the answer, no explanation.

        Context:
        %s

        Question:
        %s

        Answer:
        """.formatted(
                context,
                message
        );

        /*
         * Providing better context with memory passing the conversation ID
         * The AI knows the context belongs to the same conversation.
         */

        String answer =
                chatClient
                .prompt(prompt)
                .advisors(
                        advisor -> advisor.param(
                                ChatMemory.CONVERSATION_ID,
                                conversationId
                        )
                )
                .call()
                .content();


        List<SourceCitation> citations =   buildCitations(documents);



        return new RAGResponse(
                answer,
                citations
        );

    }


    private List<SourceCitation> buildCitations(
            List<Document> documents
    ) {

        return documents.stream()
                .map(document -> {

                    Map<String, Object> metadata =
                            document.getMetadata();


                    return new SourceCitation(
                            getMetadata(metadata, MetaDataKeys.SOURCE, String.class),
                            getMetadata(metadata, MetaDataKeys.FILE_TYPE, String.class),
                            getMetadata(metadata, MetaDataKeys.PAGE_NUMBER, Integer.class),
                            getMetadata(metadata, MetaDataKeys.CHUNK_NUMBER, Integer.class),
                            getMetadata(metadata, MetaDataKeys.DISTANCE, Double.class)
                    );

                })
                .toList();
    }
}