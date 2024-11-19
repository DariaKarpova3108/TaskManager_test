package com.example.app.dto.taskPriority;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskPriorityCreateDTO {

    @NotNull(message = "Приоритет задачи не может быть пуст")
    @Size(max = 50, message = "Размер названия приоритета не должен превышать 50 символов")
    @JsonProperty("priority_name")
    private String priorityName;
}
