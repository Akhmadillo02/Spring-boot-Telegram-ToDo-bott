package uz.ahmadillo.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.ahmadillo.springboot.dto.CodeMessage;
import uz.ahmadillo.springboot.enums.CodeMessageType;
import uz.ahmadillo.springboot.util.InlineButtonUtil;
import java.util.List;
@Component
@RequiredArgsConstructor
public class GeneralController {

    private final InlineButtonUtil inlineButtonUtil;
    public CodeMessage handle(String text, Long chatId, Integer messageId) {
        CodeMessage codeMessage = new CodeMessage();
        SendMessage sendMessage = new SendMessage();
        codeMessage.setSendMessage(sendMessage);
        sendMessage.setChatId(String.valueOf(chatId));
        if (text.equals("/start")) {
            sendMessage.setText("Assalomu alaykum *Toodo* list botga hush kelibsiz.");
            sendMessage.setParseMode("Markdown");
            InlineKeyboardButton menu = inlineButtonUtil.button("Go to menu", "menu");
            List<InlineKeyboardButton> rows = inlineButtonUtil.row(menu);
            List<List<InlineKeyboardButton>> lists = inlineButtonUtil.rowCollection(rows);
            sendMessage.setReplyMarkup(inlineButtonUtil.keyboardMarkup(lists));
            codeMessage.setType(CodeMessageType.MESSAGE);

        } else if (text.equals("/help")) {
            String msg = "*TodoList* Yordam oynasi . \n siz bu botda qilish kerak bo'lgan ishlar jadvalini tuzishingiz mumkin . \n" +
                    "malumot uchun videoni [YouTube](https://www.youtube.com/watch?v=0VkLPkIu10c&list=PLeUA5nZ_B5228g6VF3eY7Pc1N4dwq5W5K&index=9) ko'ring .\n" +
                    "yokiy mana bu videoni ko'ring";
            sendMessage.setText(msg);
            sendMessage.setParseMode("Markdown");
            sendMessage.disableWebPagePreview();

            SendVideo sendVideo = new SendVideo();
            sendVideo.setVideo(new InputFile("/home/akhmadillo02/Downloads/Telegram Desktop/odiy"));
            sendVideo.setChatId(String.valueOf(chatId));
            sendVideo.setCaption("_Bu video siz uchun juda muhim_");
            sendVideo.setParseMode("Markdown");

            codeMessage.setSendVideo(sendVideo);
            codeMessage.setSendMessage(sendMessage);
            codeMessage.setType(CodeMessageType.MESSAGE);

        } else if (text.equals("/setting")) {
            sendMessage.setText("Setting lar hali mavjud emas");
            sendMessage.setParseMode("Markdown");
            codeMessage.setType(CodeMessageType.MESSAGE);

        } else if (text.equals("menu")) {
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setText("*Assosiy Menu*");
            editMessageText.setChatId(String.valueOf(chatId));
            editMessageText.setMessageId(messageId);
            editMessageText.setParseMode("Markdown");
            editMessageText.setReplyMarkup(inlineButtonUtil.keyboardMarkup(inlineButtonUtil.rowCollection(inlineButtonUtil.row(inlineButtonUtil.button("ToDo List", "/todo/list",":clipboard:"))
            ,inlineButtonUtil.row(inlineButtonUtil.button("Create New","/todo/create","heavy_plus_sign:")))));


            codeMessage.setType(CodeMessageType.EDIT);
            codeMessage.setEditMessageText(editMessageText);
            return codeMessage;
        }
        return codeMessage;
    }
}
