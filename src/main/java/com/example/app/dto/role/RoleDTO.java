package com.example.app.dto.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {

    @JsonProperty("role_name")
    private String roleName;
}

