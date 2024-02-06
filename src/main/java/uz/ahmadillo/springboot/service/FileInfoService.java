package uz.ahmadillo.springboot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Video;
import uz.ahmadillo.springboot.dto.CodeMessage;
import uz.ahmadillo.springboot.enums.CodeMessageType;

import java.util.List;

@Service
public class FileInfoService {

    public CodeMessage getFileInfo(Message message) {
        Long chatId = message.getChatId();
        CodeMessage codeMessage = new CodeMessage();
        codeMessage.setType(CodeMessageType.MESSAGE);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        if (message.getPhoto() != null) {
            String s = this.showPhotoDetail(message.getPhoto());
            sendMessage.setText(s);
        } else if (message.getVideo() != null) {
            String s = this.showVideoDetail(message.getVideo());
            sendMessage.setText(s);
        } else {
            sendMessage.setText("Not found");
        }
        codeMessage.setSendMessage(sendMessage);
        return codeMessage;

    }

    private String showVideoDetail(Video video) {
        String s = "---------------------VIDEO INFO---------------------\n";
        s += " Size " + video.getFileSize() + " , duration = " + video.getDuration() + "ID =" + video.getFileId();

        return s;
    }

    private String showPhotoDetail(List<PhotoSize> photoSizeList) {
        String s = "---------------------PHOTO INFO---------------------\n";
        for (PhotoSize photoSize : photoSizeList) {
            s += "Size =" + photoSize.getFileSize() + " , ID = " + photoSize.getFileId() + "\n";
        }
        return s;
    }

}
