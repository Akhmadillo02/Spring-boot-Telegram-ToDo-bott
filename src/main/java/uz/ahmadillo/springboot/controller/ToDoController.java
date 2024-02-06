package uz.ahmadillo.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.ahmadillo.springboot.dto.CodeMessage;
import uz.ahmadillo.springboot.dto.TodoItem;
import uz.ahmadillo.springboot.enums.CodeMessageType;
import uz.ahmadillo.springboot.enums.ToDoItemType;
import uz.ahmadillo.springboot.repository.ToDoRepository;
import uz.ahmadillo.springboot.util.InlineButtonUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ToDoController {

    private final ToDoRepository toDoRepository;
    private final InlineButtonUtil inlineButtonUtil;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private Map<Long, TodoItem> todoItemMap = new HashMap<>();

    public CodeMessage handle(String text, Long chatId, Integer messageId) {
        CodeMessage codeMessage = new CodeMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        if (text.startsWith("/todo/")) {
            String[] commandList = text.split("/");
            String command = commandList[2];
            if (command.equals("list")) {
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setMessageId(messageId);
                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setParseMode("HTML");

                List<TodoItem> todoItemList = this.toDoRepository.getToDoList(chatId);
                StringBuilder stringBuilder = new StringBuilder("");
                if (todoItemList == null || todoItemList.isEmpty()) {
                    stringBuilder.append("Yo do not have any todo list");
                }else {
                    int count = 1;
                    for (TodoItem dto : todoItemList) {
                        stringBuilder.append("<b>" + count + "</b>");
                        stringBuilder.append("\n");
                        stringBuilder.append(dto.getTitle());
                        stringBuilder.append("\n");
                        stringBuilder.append(dto.getContent());
                        stringBuilder.append(simpleDateFormat.format(dto.getCreateDate()));
                        stringBuilder.append(" /todo_edit_" + dto.getId());
                        stringBuilder.append("\n\n");

                        count++;
                    }

                }
                editMessageText.setReplyMarkup(inlineButtonUtil.keyboardMarkup(
                        inlineButtonUtil.rowCollection(
                                inlineButtonUtil.row(inlineButtonUtil.button("Go to Menu", "menu")))));


                editMessageText.setText(stringBuilder.toString());

                codeMessage.setEditMessageText(editMessageText);
                codeMessage.setType(CodeMessageType.EDIT);

            } else if (command.equals("create")) {
                EditMessageText editMessageText = new EditMessageText();

                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setText("Send *Title*");
                editMessageText.setParseMode("Markdown");
                editMessageText.setMessageId(messageId);

                TodoItem todoItem = new TodoItem();
                todoItem.setId(String.valueOf(messageId));
                todoItem.setUserId(chatId);
                todoItem.setType(ToDoItemType.TITLE);

                this.todoItemMap.put(chatId, todoItem);

                codeMessage.setEditMessageText(editMessageText);
                codeMessage.setType(CodeMessageType.EDIT);


            }
            else if (command.equals("update")) {
                command = commandList[3];
                String id = commandList[4];
                TodoItem todoItem = this.toDoRepository.getItem(chatId, id);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setMessageId(messageId);
                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setParseMode("Markdown");

                if (command.equals("title")) {
                    editMessageText.setText("*Current Title* " + todoItem.getTitle() + "\nPlease send new Title.");
                    editMessageText.setReplyMarkup(inlineButtonUtil.keyboardMarkup(inlineButtonUtil.rowCollection(inlineButtonUtil.row(inlineButtonUtil.button("Cansel","/todo/cancel")))));
                    codeMessage.setEditMessageText(editMessageText);
                    codeMessage.setType(CodeMessageType.EDIT);
                    todoItem.setType(ToDoItemType.UPDATE_TITLE);
                    todoItemMap.put(chatId, todoItem);

                }
                else if (command.equals("content")) {
                    editMessageText.setText("*Current Content* " + todoItem.getContent() + "\nPlease send new Content.");
                    codeMessage.setEditMessageText(editMessageText);
                    codeMessage.setType(CodeMessageType.EDIT);
                    todoItem.setType(ToDoItemType.UPDATE_CONTENT);
                    todoItemMap.put(chatId, todoItem);
                }
                editMessageText.setReplyMarkup(inlineButtonUtil.keyboardMarkup(
                        inlineButtonUtil.rowCollection(
                                inlineButtonUtil.row(inlineButtonUtil.button("Cancel", "/todo/cancel")))));

            }
            else if (command.equals("cancel")) {
                this.todoItemMap.remove(chatId);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setMessageId(messageId);
                editMessageText.setText("Update Was Cancelled");
                editMessageText.setReplyMarkup(inlineButtonUtil.keyboardMarkup(
                        inlineButtonUtil.rowCollection(
                                inlineButtonUtil.row(inlineButtonUtil.button("ToDo List", "/todo/list")),
                                inlineButtonUtil.row(inlineButtonUtil.button("Go to Menu", "menu")))));

                codeMessage.setEditMessageText(editMessageText);
                codeMessage.setType(CodeMessageType.EDIT);

            }
            else if (command.equals("delete")) {
                String id = commandList[3];
                boolean result = this.toDoRepository.delete(chatId, id);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setMessageId(messageId);
                editMessageText.setChatId(String.valueOf(chatId));

                if (result) {
                    editMessageText.setText("ToDo Was Deleted");
                }else {
                    editMessageText.setText("Error");
                }
                editMessageText.setReplyMarkup(inlineButtonUtil.keyboardMarkup(
                        inlineButtonUtil.rowCollection(
                                inlineButtonUtil.row(inlineButtonUtil.button("ToDo List", "/todo/list")),
                                inlineButtonUtil.row(inlineButtonUtil.button("Go to Menu", "menu")))));

                codeMessage.setEditMessageText(editMessageText);
                codeMessage.setType(CodeMessageType.EDIT);
            }
            return codeMessage;
        }

        if (text.startsWith("/todo_")) {
            String todoId = text.split("/todo_edit_")[1];
            TodoItem todoItem = this.toDoRepository.getItem(chatId, todoId);
            if (todoItem == null) {
                sendMessage.setText("Bu nimasi bo'ldi bunaqa id yo'qku");
            } else {
                sendMessage.setText(todoItem.getTitle() + "\n" + todoItem.getContent() + "\n" + "_" + simpleDateFormat.format(todoItem.getCreateDate()) + "_");
                sendMessage.setParseMode("Markdown");

                List<List<InlineKeyboardButton>> rowCollections =
                        inlineButtonUtil.rowCollection(
                                inlineButtonUtil.row(
                                        inlineButtonUtil.button("Update Title", "/todo/update/title/" + todoItem.getId()),
                                        inlineButtonUtil.button("Update Content", "/todo/update/content/" + todoItem.getId()),
                                        inlineButtonUtil.button("Delete", "/todo/delete/" + todoItem.getId())),
                                inlineButtonUtil.row(inlineButtonUtil.button("ToDo List", "/todo/list")));


                sendMessage.setReplyMarkup(inlineButtonUtil.keyboardMarkup(rowCollections));


            }

            codeMessage.setSendMessage(sendMessage);
            codeMessage.setType(CodeMessageType.MESSAGE);

            return codeMessage;
        }


        if (this.todoItemMap.containsKey(chatId)) {
            TodoItem todoItem = this.getTodoItemMap().get(chatId);
            sendMessage.setParseMode("Markdown");
            codeMessage.setSendMessage(sendMessage);
            codeMessage.setType(CodeMessageType.MESSAGE);


            if (todoItem.getType().equals(ToDoItemType.TITLE)) {
                todoItem.setTitle(text);
                sendMessage.setText("*Title* " + todoItem.getTitle() + "\n" + "Send *Content*");
                todoItem.setType(ToDoItemType.CONTENT);

            }
            else if (todoItem.getType().equals(ToDoItemType.CONTENT)) {
                todoItem.setContent(text);
                todoItem.setCreateDate(new Date());
                todoItem.setType(ToDoItemType.FINISHED);
                int n = this.toDoRepository.add(chatId, todoItem);
                todoItemMap.remove(chatId);

                sendMessage.setText("ItemCount" + n + "\n*Title* " + todoItem.getTitle() + "\n" + "*Content*:" + todoItem.getContent() + "\n" + "Create ToDo finished.");

                sendMessage.setReplyMarkup(inlineButtonUtil.keyboardMarkup(
                        inlineButtonUtil.rowCollection(
                                inlineButtonUtil.row(inlineButtonUtil.button("ToDo List", "/todo/list", ":clipboard:")),
                                inlineButtonUtil.row(inlineButtonUtil.button("Go to Menu", "menu")))));
            }
            else if (todoItem.getType().equals(ToDoItemType.UPDATE_TITLE)) {
                todoItem.setTitle(text);
                this.todoItemMap.remove(chatId);
                sendMessage.setText("*Title* " + todoItem.getTitle() + "\n" + "*Content*: " + todoItem.getContent());
                sendMessage.setReplyMarkup(getToDoKeyBoard(todoItem.getId()));
            }
            else if (todoItem.getType().equals(ToDoItemType.UPDATE_CONTENT)) {
                todoItem.setContent(text);
                this.todoItemMap.remove(chatId);
                sendMessage.setText("*Title* " + todoItem.getTitle() + "\n" + "*Content*: " + todoItem.getContent());
                sendMessage.setReplyMarkup(getToDoKeyBoard(todoItem.getId()));
            }
        }

        return codeMessage;
    }

    private InlineKeyboardMarkup getToDoKeyBoard(String id) {
        List<List<InlineKeyboardButton>> rowCollections =
                inlineButtonUtil.rowCollection(
                inlineButtonUtil.row(
                        inlineButtonUtil.button("Update Title", "/todo/update/title/" + id),
                        inlineButtonUtil.button("Update Content", "/todo/update/content/" + id),
                        inlineButtonUtil.button("Delete", "/todo/delete/" + id)),
                inlineButtonUtil.row(inlineButtonUtil.button("ToDo List", "/todo/list")));

        return inlineButtonUtil.keyboardMarkup(rowCollections);
    }




    public Map<Long, TodoItem> getTodoItemMap() {
        return todoItemMap;
    }

    public void setTodoItemMap(Map<Long, TodoItem> todoItemMap) {
        this.todoItemMap = todoItemMap;
    }
}
