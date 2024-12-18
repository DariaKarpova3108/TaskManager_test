package com.example.app.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskCreateDTO {

    @NotNull(message = "Название задачи не может быть пустым")
    @Size(max = 50, message = "Название задачи не должно превышать 50 символов")
    @JsonProperty("title")
    private String title;

    @NotNull(message = "Описание задачи не может быть пустым")
    @Size(max = 50, message = "Описание задачи не должно превышать 50 символов")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "Статус задачи не может быть пустым")
    @JsonProperty("status")
    private String status;

    @NotNull(message = "Приоритет задачи не может быть пуст")
    @JsonProperty("priority")
    private String priority;

    @NotNull(message = "Автор задачи должен быть указан обязательно")
    @JsonProperty("author_id")
    private Long authorId;

    @NotNull(message = "Исполнитель задачи должен быть указан обязательно")
    @JsonProperty("assignee_id")
    private Long assigneeId;
}
