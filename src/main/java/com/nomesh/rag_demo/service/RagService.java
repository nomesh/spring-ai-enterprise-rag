package com.nomesh.rag_demo.service;

import com.nomesh.rag_demo.retrieval.DocumentRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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


    public String ask(
            String conversationId,
            String message
    ) {


        List<Document> documents =
                documentRetriever.retrieve(message);


        if (documents.isEmpty()) {

            return "I could not find relevant information in the company knowledge base.";
        }


        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        System.out.println("========== RAG CONTEXT ==========");
        System.out.println(context);
        System.out.println("==================================");

        documents.forEach(document -> {
            System.out.println("===== DOCUMENT METADATA =====");
            System.out.println(document.getMetadata());
        });


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
        return chatClient
                .prompt(prompt)
                .advisors(
                        advisor -> advisor.param(
                                ChatMemory.CONVERSATION_ID,
                                conversationId
                        )
                )
                .call()
                .content();
    }
}