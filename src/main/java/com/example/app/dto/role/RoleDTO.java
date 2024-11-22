package com.example.app.dto.role;

import com.example.app.models.RoleName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {

    @JsonProperty("role_name")
    private RoleName roleName;
}

