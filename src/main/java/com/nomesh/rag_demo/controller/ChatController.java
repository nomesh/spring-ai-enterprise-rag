package com.nomesh.rag_demo.controller;

import com.nomesh.rag_demo.service.RagService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/chat")
public class ChatController {


    private final RagService ragService;


    public ChatController(RagService ragService) {

        this.ragService = ragService;
    }


    @GetMapping
    public String chat(
            @RequestParam String conversationId,
            @RequestParam String message
    ) {

        return ragService.ask(
                conversationId,
                message
        );
    }
}