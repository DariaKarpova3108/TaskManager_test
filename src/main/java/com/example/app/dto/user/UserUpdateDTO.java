package com.example.app.dto.user;

import com.example.app.dto.role.RoleDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class UserUpdateDTO {

    @JsonProperty("first_name")
    @NotNull(message = "Имя не должно быть пустым")
    private JsonNullable<String> firstName;

    @JsonProperty("last_name")
    @NotNull(message = "Фамилия не должна быть пустой")
    private JsonNullable<String> lastName;

    @JsonProperty("email")
    @NotNull(message = "Пароль не должен быть пустым")
    private JsonNullable<String> email;

    @JsonProperty("password")
    @NotNull(message = "Пароль не может состоять из 0 символов")
    private JsonNullable<String> password;

    //    @JsonProperty("role")
//    private JsonNullable<RoleDTO> role;
    @JsonProperty("roles")
    @NotNull(message = "Укажите хотя бы одну роль")
    private JsonNullable<Set<RoleDTO>> roles;
}
