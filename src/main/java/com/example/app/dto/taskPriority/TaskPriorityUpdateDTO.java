package com.example.app.dto.taskPriority;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;
@Getter
@Setter
@ToString
public class TaskPriorityUpdateDTO {

    @NotNull(message = "Приоритет задачи не может быть пуст")
    @JsonProperty("priority_name")
    private JsonNullable<String> priorityName;
}
