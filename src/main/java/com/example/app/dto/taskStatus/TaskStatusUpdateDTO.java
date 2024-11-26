package com.example.app.dto.taskStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
@ToString
public class TaskStatusUpdateDTO {

    @NotNull(message = "Статус задачи не может быть пуст")
    @JsonProperty("status_name")
    private JsonNullable<String> statusName;
}
