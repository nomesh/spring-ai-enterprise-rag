package com.nomesh.rag.controller;

import com.nomesh.rag.model.RAGResponse;
import com.nomesh.rag.service.RagService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/chat")
public class ChatController {


    private final RagService ragService;


    public ChatController(RagService ragService) {

        this.ragService = ragService;
    }


    @GetMapping
    public RAGResponse chat(
            @RequestParam String conversationId,
            @RequestParam String message
    ) {

        return ragService.ask(
                conversationId,
                message
        );
    }
}