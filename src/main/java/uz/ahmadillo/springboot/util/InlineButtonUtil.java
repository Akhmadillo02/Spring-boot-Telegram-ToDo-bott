package uz.ahmadillo.springboot.util;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class InlineButtonUtil {

    public InlineKeyboardButton button(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    public InlineKeyboardButton button(String text, String callbackData, String emoji) {
        String emojiText = EmojiParser.parseToUnicode(emoji + " " + text);
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    public  List<InlineKeyboardButton> row(InlineKeyboardButton... inlineKeyboardButtons) {
        List<InlineKeyboardButton> row = new LinkedList<>();
        row.addAll(Arrays.asList(inlineKeyboardButtons));
        return row;
    }

    public List<List<InlineKeyboardButton>> rowCollection(List<InlineKeyboardButton>... rows) {
        List<List<InlineKeyboardButton>> rowCollection = new LinkedList<>();
        rowCollection.addAll(Arrays.asList(rows));
        return rowCollection;
    }

    public InlineKeyboardMarkup keyboardMarkup(List<List<InlineKeyboardButton>> collection) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(collection);
        return inlineKeyboardMarkup;
    }
}
