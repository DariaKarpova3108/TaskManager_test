package com.example.app.dto.taskStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusCreateDTO {

    @NotNull(message = "Статус задачи не может быть пуст")
    @Size(max = 50, message = "Название статуса не должно превышать 50 символов")
    @JsonProperty("status_name")
    private String statusName;
}
