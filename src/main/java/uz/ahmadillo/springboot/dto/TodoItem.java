package uz.ahmadillo.springboot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import uz.ahmadillo.springboot.enums.ToDoItemType;

import java.util.Date;
@Data
@NoArgsConstructor
public class TodoItem {

    private String id;
    private String title;
    private String content;
    private Date createDate;
    private Long userId;
    private ToDoItemType type;
}
