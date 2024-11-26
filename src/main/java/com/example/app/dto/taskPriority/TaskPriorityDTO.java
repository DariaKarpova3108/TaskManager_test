package com.example.app.dto.taskPriority;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskPriorityDTO {

    @JsonProperty("id")
    private Long id;

   @JsonProperty("priority_name")
    private String priorityName;
}
