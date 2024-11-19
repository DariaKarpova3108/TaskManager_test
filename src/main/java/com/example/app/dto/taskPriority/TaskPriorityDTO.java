package com.example.app.dto.taskPriority;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskPriorityDTO {

    @JsonProperty("id")
    private Long id;

   @JsonProperty("priority_name")
    private String priorityName;
}
