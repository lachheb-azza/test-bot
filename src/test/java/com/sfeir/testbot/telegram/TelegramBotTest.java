package com.sfeir.testbot.telegram;

import com.sfeir.testbot.agents.AIAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramBotTest {

    @Mock
    private AIAgent aiAgent;

    private TelegramBot bot;

    @BeforeEach
    void setUp() {
        bot = spy(new TelegramBot(aiAgent));
        ReflectionTestUtils.setField(bot, "telegramBotToken", "TOKEN-123");
    }

    @Test
    void getBotUsername_returnsConfiguredName() {
        assertThat(bot.getBotUsername()).isEqualTo("SfeirAIBot");
    }

    @Test
    void getBotToken_returnsInjectedToken() {
        assertThat(bot.getBotToken()).isEqualTo("TOKEN-123");
    }

    @Test
    void onUpdateReceived_ignoresUpdateWithoutMessage() {
        Update update = org.mockito.Mockito.mock(Update.class);
        when(update.hasMessage()).thenReturn(false);

        bot.onUpdateReceived(update);

        verify(aiAgent, never()).askAgent(any());
    }

    @Test
    void onUpdateReceived_sendsAgentAnswerBackToTheChat() throws TelegramApiException {
        Update update = org.mockito.Mockito.mock(Update.class);
        Message message = org.mockito.Mockito.mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("bonjour");
        when(message.getChatId()).thenReturn(42L);
        when(message.getPhoto()).thenReturn(null);
        when(aiAgent.askAgent(any(Prompt.class))).thenReturn("salut");

        // Prevent the real Telegram API calls.
        doReturn(null).when(bot).execute(any(SendChatAction.class));
        doReturn(null).when(bot).execute(any(SendMessage.class));

        bot.onUpdateReceived(update);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(messageCaptor.capture());
        SendMessage sent = messageCaptor.getValue();
        assertThat(sent.getText()).isEqualTo("salut");
        assertThat(sent.getChatId()).isEqualTo("42");
    }

    @Test
    void onUpdateReceived_sendsTypingActionBeforeAnswering() throws TelegramApiException {
        Update update = org.mockito.Mockito.mock(Update.class);
        Message message = org.mockito.Mockito.mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("hello");
        when(message.getChatId()).thenReturn(7L);
        when(message.getPhoto()).thenReturn(null);
        when(aiAgent.askAgent(any(Prompt.class))).thenReturn("hi");

        doReturn(null).when(bot).execute(any(SendChatAction.class));
        doReturn(null).when(bot).execute(any(SendMessage.class));

        bot.onUpdateReceived(update);

        verify(bot).execute(any(SendChatAction.class));
    }
}
