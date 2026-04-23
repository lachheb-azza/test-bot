package com.sfeir.testbot.web;

import com.sfeir.testbot.agents.AIAgent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
public class ChatController {
    private AIAgent aiAgent;

    public ChatController(AIAgent aiAgent) {
        this.aiAgent = aiAgent;
    }

    @GetMapping("/chat")
    public String chat(String query) {
        return aiAgent.askAgent(query);
    }
}
