package com.sfeir.testbot.web;

import com.sfeir.testbot.agents.AIAgent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.prompt.Prompt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private AIAgent aiAgent;

    @InjectMocks
    private ChatController chatController;

    @Test
    void chat_delegatesQueryToAgentAndReturnsAnswer() {
        when(aiAgent.askAgent(org.mockito.ArgumentMatchers.any(Prompt.class)))
                .thenReturn("Bonjour");

        String result = chatController.chat("Quelle heure est-il ?");

        assertThat(result).isEqualTo("Bonjour");
    }

    @Test
    void chat_buildsPromptFromTheGivenQuery() {
        when(aiAgent.askAgent(org.mockito.ArgumentMatchers.any(Prompt.class)))
                .thenReturn("ok");

        chatController.chat("hello world");

        ArgumentCaptor<Prompt> promptCaptor = ArgumentCaptor.forClass(Prompt.class);
        verify(aiAgent).askAgent(promptCaptor.capture());
        assertThat(promptCaptor.getValue().getContents()).contains("hello world");
    }

    @Test
    void chat_propagatesNullQuery() {
        when(aiAgent.askAgent(org.mockito.ArgumentMatchers.any(Prompt.class)))
                .thenReturn("JE NE SAIS PAS");

        String result = chatController.chat(null);

        assertThat(result).isEqualTo("JE NE SAIS PAS");
    }
}
