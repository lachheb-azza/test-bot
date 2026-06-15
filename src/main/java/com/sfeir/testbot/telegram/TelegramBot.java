package com.sfeir.testbot.telegram;

import com.sfeir.testbot.agents.AIAgent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        var chatId = telegramRequest.getMessage().getChatId();
        var mediaData = getMediaData(telegramRequest);
        var query = messageText != null ? messageText : mediaData.caption();
        var userMessage = UserMessage.builder()
                .text(query)
                .media(mediaData.mediaList())
                .build();
        try {
            sendTypingAction(chatId);
            var answer = aiAgent.askAgent(new Prompt(userMessage));
            sendTextMessage(chatId, answer);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private MediaData getMediaData(Update telegramRequest) {
        List<PhotoSize> photos = telegramRequest.getMessage().getPhoto();
        List<Media> mediaList = new ArrayList<>();
        String caption = null;
        if (photos != null) {
            caption = telegramRequest.getMessage().getCaption();
            if (caption == null) caption = "What do you see in this image";

            for (PhotoSize ps : photos) {
                String fileId = ps.getFileId();
                GetFile getFile = new GetFile();
                getFile.setFileId(fileId);
                try {
                    File file = execute(getFile);

                    String filePath = file.getFilePath();
                    String textUrl = "https://api.telegram.org/file/bot"
                            + getBotToken() + "/" + filePath;
                    URL fileUrl = new URL(textUrl);
                    mediaList.add(Media.builder()
                            .id(fileId)
                            .mimeType(MimeTypeUtils.IMAGE_PNG)
                            .data(new UrlResource(fileUrl))
                            .build());
                } catch (MalformedURLException | TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return new MediaData(mediaList, caption);
    }

    //envoyer la reponse vers telegram
    private void sendTextMessage(long chatId, String text) throws TelegramApiException {
        var sendMessage = new SendMessage(String.valueOf(chatId), text);
        execute(sendMessage);
    }

    private void sendTypingAction(long chatId) throws TelegramApiException {
        var action = new SendChatAction();
        action.setChatId(chatId);
        action.setAction(ActionType.TYPING);
        execute(action);
    }

    @Override
    public String getBotUsername() {
        return "SfeirAIBot";
    }

    @Override
    public String getBotToken() {
        return telegramBotToken;
    }

    private record MediaData(List<Media> mediaList, String caption) {
    }
}
