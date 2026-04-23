package com.sfeir.testbot.telegram;

import com.sfeir.testbot.agents.AIAgent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

//composant qui va communiquer avec telegram (com web classique)
//subscribe à telegram API
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final AIAgent aiAgent;

    @Value("${telegram.api.key}")
    private String telegramBotToken;

    // methode qui s'execute juste apres le constructeur
    @PostConstruct
    public void registerTelegramBot() {
        try {
            var api = new TelegramBotsApi(DefaultBotSession.class);
            // faire un subscribe vers l'api en utilisant la clé via le username et la key
            api.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    // s'execute quand un client tele envoie un message
    @Override
    public void onUpdateReceived(Update telegramRequest) {
        if (!telegramRequest.hasMessage())
            return;
        var messageText = telegramRequest.getMessage().getText();
        var answer = aiAgent.askAgent(messageText);
        try {
            sendTextMessage(telegramRequest.getMessage().getChatId(), answer);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    //envoyer la reponse vers telegram
    private void sendTextMessage(long chatId, String text) throws TelegramApiException {
        var sendMessage = new SendMessage(String.valueOf(chatId), text);
        execute(sendMessage);
    }

    //
    @Override
    public String getBotUsername() {
        return "SfeirAIBot";
    }

    @Override
    public String getBotToken() {
        return telegramBotToken;
    }
}
