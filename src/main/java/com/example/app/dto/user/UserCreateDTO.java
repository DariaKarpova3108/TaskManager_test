package com.example.app.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserCreateDTO {

    @JsonProperty("first_name")
    @NotNull(message = "Имя не должно быть пустым")
    @Size(max = 50, message = "Имя не должно превышать 50 символов")
    private String firstName;

    @JsonProperty("last_name")
    @NotNull(message = "Фамилия не должна быть пустой")
    @Size(max = 50, message = "Фамилия не должна превышать 50 символов")
    private String lastName;

    @NotNull(message = "Пароль не должен быть пустым")
    @JsonProperty("password")
    private String password;

    @NotNull(message = "Email не должен быть пустым")
    @JsonProperty("email")
    private String email;
}
