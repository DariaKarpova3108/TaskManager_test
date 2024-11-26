package com.example.app.dto.taskStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskStatusDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("status_name")
    private String statusName;
}
