package uz.ahmadillo.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.ahmadillo.springboot.dto.CodeMessage;
import uz.ahmadillo.springboot.service.FileInfoService;


@Component
@RequiredArgsConstructor
public class TooDooBotController extends TelegramLongPollingBot {

    private final GeneralController generalController;
    private final FileInfoService fileInfoService;
    private final ToDoController toDoController;

    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String botToke;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return botToke;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();


        try {
            if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String data = callbackQuery.getData();
                message = callbackQuery.getMessage();
                if (data.equals("menu")) {
                    sendMsg(this.generalController.handle(data, message.getChatId(), message.getMessageId()));
                }
                if (data.startsWith("/todo")) {
                    sendMsg(this.toDoController.handle(data, message.getChatId(), message.getMessageId()));

                }
            } else if (message != null) {
                String text = message.getText();

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(String.valueOf(message.getChatId()));
                Integer messageId = message.getMessageId();
                if (text != null) {
                    if (text.equals("/start") || text.equals("/help") || text.equals("/setting")) {
                        this.sendMsg(this.generalController.handle(text, message.getChatId(), messageId));
                    } else if (this.toDoController.getTodoItemMap().containsKey(message.getChatId()) || text.startsWith("/todo_")) {
                        this.sendMsg(this.toDoController.handle(text, message.getChatId(), messageId));
                    }
                    else {

                        sendMessage.setText("Mavjud emas");
                        sendMsg(sendMessage);
                    }
                } else {
                    sendMsg(fileInfoService.getFileInfo(message));
                }
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public void sendMsg(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMsg(CodeMessage codeMessage) {
        try {
            switch (codeMessage.getType()) {
                case MESSAGE:
                    execute(codeMessage.getSendMessage());
                    break;
                case EDIT:
                    execute(codeMessage.getEditMessageText());
                    break;
                case MESSAGE_VIDEO:
                    execute(codeMessage.getSendMessage());
                    execute(codeMessage.getSendVideo());
                    break;
                default:
                    break;
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}







