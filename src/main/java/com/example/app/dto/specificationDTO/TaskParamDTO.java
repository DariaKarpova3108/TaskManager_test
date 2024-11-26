package com.example.app.dto.specificationDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskParamDTO {
    private Long authorId;
    private Long assigneeId;
    private String statusCont;
    private String priorityCont;
}
