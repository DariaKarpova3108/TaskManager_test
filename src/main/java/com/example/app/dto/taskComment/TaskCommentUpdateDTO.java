package com.example.app.dto.taskComment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskCommentUpdateDTO {

    @JsonProperty("author_id")
    private JsonNullable<Long> authorId;

    @JsonProperty("title")
    private JsonNullable<String> title;

    @JsonProperty("description")
    private JsonNullable<String> description;

    @JsonProperty("task_id")
    private JsonNullable<Long> taskId;
}
