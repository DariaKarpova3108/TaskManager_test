package com.example.app.dto.user;

import com.example.app.dto.role.RoleDTO;
import com.example.app.dto.task.TaskDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class UserDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("roles")
    private Set<RoleDTO> roles;

    @JsonProperty("tasks_as_author")
    private List<TaskDTO> tasksAsAuthor;

    @JsonProperty("tasks_as_assignee")
    private List<TaskDTO> tasksAsAssignee;
}
