package com.example.app.dto.taskPriority;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;
@Getter
@Setter
public class TaskPriorityUpdateDTO {

    @JsonProperty("priority_name")
    private JsonNullable<String> priorityName;
}
