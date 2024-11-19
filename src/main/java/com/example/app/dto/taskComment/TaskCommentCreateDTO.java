package com.example.app.dto.taskComment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCommentCreateDTO {

    @NotNull(message = "Укажите автора комментария")
    @JsonProperty("author_id")
    private Long authorId;

    @Size(max = 50, message = "Размер заголовка для комментария не должен превышать 50 символов")
    @JsonProperty("title")
    private String title;

    @NotNull(message = "Описание комментария не может быть пустым")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "Укажите id задачи")
    @JsonProperty("task_id")
    private Long taskId;
}
