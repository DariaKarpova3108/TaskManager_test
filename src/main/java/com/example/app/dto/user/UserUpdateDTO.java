package com.example.app.dto.user;

import com.example.app.dto.role.RoleDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class UserUpdateDTO {

    @JsonProperty("first_name")
    private JsonNullable<String> firstName;

    @JsonProperty("last_name")
    private JsonNullable<String> lastName;

    @JsonProperty("email")
    private JsonNullable<String> email;

    @JsonProperty("password")
    private JsonNullable<String> password;

    @JsonProperty("roles")
    private JsonNullable<Set<RoleDTO>> roles;
}
