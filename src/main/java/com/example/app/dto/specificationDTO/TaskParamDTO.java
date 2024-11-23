package com.example.app.dto.specificationDTO;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TaskParamDTO {
    private Long authorId;
    private Long assigneeId;
    private String statusCont;
    private String priorityCont;
}
