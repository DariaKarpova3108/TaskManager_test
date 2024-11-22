package com.example.app.dto.taskComment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskCommentUpdateDTO {

    @NotNull(message = "Id автора комментария должен содержать значение")
    @JsonProperty("author_id")
    private JsonNullable<Long> authorId;

    @JsonProperty("title")
    @Size(max = 50, message = "Размер заголовка для комментария не должен превышать 50 символов")
    private JsonNullable<String> title;

    @JsonProperty("description")
    @NotNull(message = "Описание комментария не может быть пустым")
    private JsonNullable<String> description;
}
