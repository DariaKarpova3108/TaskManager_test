package com.example.app.dto.task;

import com.example.app.dto.taskComment.TaskCommentDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Getter
@Setter
public class TaskUpdateDTO {

    @JsonProperty("title")
    private JsonNullable<String> title;

    @JsonProperty("description")
    private JsonNullable<String> description;

    @JsonProperty("status")
    private JsonNullable<String> status;

    @JsonProperty("priority")
    private JsonNullable<String> priority;

    @JsonProperty("task_comments")
    private JsonNullable<List<TaskCommentDTO>> taskComments;

    @JsonProperty("author_id")
    private JsonNullable<Long> authorId;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;
}
