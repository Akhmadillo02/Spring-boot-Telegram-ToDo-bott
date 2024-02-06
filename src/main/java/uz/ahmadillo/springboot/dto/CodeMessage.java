package uz.ahmadillo.springboot.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import uz.ahmadillo.springboot.enums.CodeMessageType;

import javax.swing.plaf.PanelUI;

@Component
@Data
public class CodeMessage {

    public CodeMessageType type;
    private SendMessage sendMessage;
    private EditMessageText editMessageText;
    private SendVideo sendVideo;
}
