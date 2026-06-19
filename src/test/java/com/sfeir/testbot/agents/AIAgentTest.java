package com.sfeir.testbot.agents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIAgentTest {

    @Mock
    private ToolCallbackProvider tools;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Mock
    private ChatMemory memory;

    // RETURNS_SELF makes every fluent builder call return the same builder mock.
    @Mock(answer = Answers.RETURNS_SELF)
    private ChatClient.Builder builder;

    private AIAgent buildAgent() {
        when(builder.build()).thenReturn(chatClient);
        return new AIAgent(builder, memory, tools);
    }

    @Test
    void constructor_iteratesToolCallbacksWithoutFailing() {
        ToolCallback callback = mock(ToolCallback.class, Answers.RETURNS_DEEP_STUBS);
        when(tools.getToolCallbacks()).thenReturn(new ToolCallback[]{callback});

        AIAgent agent = buildAgent();

        assertThat(agent).isNotNull();
        verify(tools).getToolCallbacks();
    }

    @Test
    void askAgent_returnsContentFromChatClient() {
        when(tools.getToolCallbacks()).thenReturn(new ToolCallback[0]);
        when(chatClient.prompt(any(Prompt.class)).call().content()).thenReturn("la réponse");

        AIAgent agent = buildAgent();
        String answer = agent.askAgent(new Prompt("une question"));

        assertThat(answer).isEqualTo("la réponse");
    }

    @Test
    void askAgent_forwardsTheGivenPromptToTheChatClient() {
        when(tools.getToolCallbacks()).thenReturn(new ToolCallback[0]);
        when(chatClient.prompt(any(Prompt.class)).call().content()).thenReturn("ok");

        AIAgent agent = buildAgent();
        Prompt prompt = new Prompt("salut");
        agent.askAgent(prompt);

        verify(chatClient).prompt(prompt);
    }
}