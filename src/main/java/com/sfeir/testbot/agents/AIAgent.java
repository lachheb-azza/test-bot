package com.sfeir.testbot.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AIAgent {
    private ChatClient chatClient;

    public AIAgent(ChatClient.Builder builder, ChatMemory memory, ToolCallbackProvider tools) {
        Arrays.stream((tools.getToolCallbacks()))
                .forEach(toolCallback -> {
                    System.out.println("---------------");
                    System.out.println(toolCallback.getToolDefinition());
                    System.out.println("---------------");
                });
        this.chatClient = builder
                .defaultSystem("""
                        Vous êtes un assistant qui se charge de répondre aux questions des utilisateurs en fonction du contexte.
                        Si aucun contexte n'est donné, vous repondez avec JE NE SAIS PAS.
                        """)
                .defaultToolCallbacks(tools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .build();
    }


    public String askAgent(String query) {
        return chatClient.prompt()
                .user(query)
                .call()
                .content();
    }
}
