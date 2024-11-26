package com.example.app.dto.taskComment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskCommentCreateDTO {

    @NotNull(message = "Id автора комментария должен содержать значение")
    @JsonProperty("author_id")
    private Long authorId;

    @Size(max = 50, message = "Размер заголовка для комментария не должен превышать 50 символов")
    @JsonProperty("title")
    private String title;

    @NotNull(message = "Описание комментария не может быть пустым")
    @JsonProperty("description")
    private String description;
}
