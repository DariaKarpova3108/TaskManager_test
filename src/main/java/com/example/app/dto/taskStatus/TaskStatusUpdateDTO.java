package com.example.app.dto.taskStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskStatusUpdateDTO {

    @JsonProperty("status_name")
    private JsonNullable<String> statusName;
}
