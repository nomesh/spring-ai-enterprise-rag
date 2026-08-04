package com.nomesh.rag_demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RagService {

    private final ChatClient chatClient;

    public RagService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String ask(String question) {

        String template = """
                You are an expert Java and Spring AI architect.
                
                Answer the user question clearly.
                
                Follow these rules:
                - Explain concepts step by step
                - Provide examples where useful
                - Mention best practices
                - Avoid making assumptions
                
                User question:
                {question}
                """;


        PromptTemplate promptTemplate = new PromptTemplate(template);

        String prompt =
                promptTemplate.render(
                        Map.of(
                                "question",
                                question
                        )
                );

        return chatClient
                .prompt()
                .system("""
                        You are a helpful enterprise software architect.
                        You specialize in Java, Spring Boot,
                        cloud architecture and AI systems.
                        """)
                .user(prompt)
                .call()
                .content();
    }
}
