package com.example.app.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Getter
@Setter
public class TaskUpdateDTO {

    @NotNull(message = "Название задачи не может быть пустым")
    @Size(max = 50, message = "Название задачи не должно превышать 50 символов")
    @JsonProperty("title")
    private JsonNullable<String> title;

    @NotNull(message = "Описание задачи не может быть пустым")
    @Size(max = 50, message = "Описание задачи не должно превышать 50 символов")
    @JsonProperty("description")
    private JsonNullable<String> description;

    @NotNull(message = "Статус задачи не может быть пустым")
    @JsonProperty("status")
    private JsonNullable<String> status;

    @JsonProperty("priority")
    @NotNull(message = "Приоритет задачи не может быть пуст")
    private JsonNullable<String> priority;

    @JsonProperty("task_comments")
    private JsonNullable<List<Long>> taskCommentsId;

    @JsonProperty("author_id")
    @NotNull(message = "Автор задачи должен быть указан обязательно")
    private JsonNullable<Long> authorId;

    @JsonProperty("assignee_id")
    @NotNull(message = "Исполнитель задачи должен быть указан обязательно")
    private JsonNullable<Long> assigneeId;
}
