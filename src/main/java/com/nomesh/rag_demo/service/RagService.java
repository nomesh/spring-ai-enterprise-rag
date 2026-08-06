package com.nomesh.rag_demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;


import java.util.Map;

@Service
public class RagService {


    private final ChatClient chatClient;


    public RagService(ChatClient.Builder builder) {


        ChatMemory chatMemory =
                MessageWindowChatMemory
                .builder().chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();


        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .build())
                .build();
    }


    public String ask(String conversationId, String question) {


        String template = """
                You are an enterprise Java architect.
                
                Answer clearly and professionally.
                
                Question:
                {question}
                """;


        PromptTemplate promptTemplate = new PromptTemplate(template);

        String prompt = promptTemplate.render(Map.of("question", question));

        return chatClient.prompt().advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .system("""
                    You are a senior software architect
                    specializing in Spring Boot,
                    cloud systems and AI.
                    """)
                .user(prompt)
                .call()
                .content();
    }
}