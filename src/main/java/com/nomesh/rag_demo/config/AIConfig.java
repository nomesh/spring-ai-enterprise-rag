package com.nomesh.rag_demo.config;

import com.nomesh.rag_demo.utils.RAGutils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    /**
     * Notice that you're injecting ChatModel, not OllamaChatModel.
     * Spring Boot already creates the ChatModel bean from your configuration:
     * @param chatModel
     * @return
     */

    @Bean
    ChatClient simpleChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

//    @Bean
//    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
//        return MessageWindowChatMemory.builder()
//                .maxMessages(5)
//                .chatMemoryRepository(jdbcChatMemoryRepository)
//                .build();
//    }



    @Bean
    public ChatClient chatClient(ChatClient.Builder clientBuilder, ChatMemory chatMemory) {
        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return clientBuilder
                .defaultAdvisors(loggerAdvisor, memoryAdvisor)
                .build();
    }
}