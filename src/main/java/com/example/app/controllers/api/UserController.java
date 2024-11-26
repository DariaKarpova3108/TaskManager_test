package com.example.app.controllers.api;

import com.example.app.dto.user.UserCreateDTO;
import com.example.app.dto.user.UserDTO;
import com.example.app.dto.user.UserUpdateDTO;
import com.example.app.services.UserService;
import com.example.app.utils.UserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Контроллер пользователей", description = "Позволяет проводить CRUD операции с пользователями")
public class UserController {
    private final UserService userService;
    private final UserUtils userUtils;

    @Operation(
            summary = "Получить список пользователей",
            description = "Возвращает список всех пользователей")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<UserDTO>> getListUsers() {
        log.info("Fetching all users");
        return ResponseEntity.ok()
                .header("X-TotalCount", String.valueOf(userService.getAllUsers().size()))
                .body(userService.getAllUsers());
    }

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по указанному ID")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public UserDTO getUser(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        return userService.getUser(id);
    }

    @Operation(
            summary = "Создать нового пользователя",
            description = "Создает нового пользователя")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public UserDTO createUser(@RequestBody @Valid UserCreateDTO createDTO) {
        log.info("Request to create user: {}", createDTO);
        var userDTO = userService.createUser(createDTO);
        log.info("User created successfully: {}", userDTO);
        return userDTO;
    }

    @Operation(
            summary = "Обновить пользователя",
            description = "Обновляет данные пользователя по указанному ID, "
                    + "операция доступна только администратору и пользователю чей ID совпадает с запрашиваемым")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @userUtils.checkCurrentUser(#id)")
    public UserDTO updateUser(@RequestBody @Valid UserUpdateDTO updateDTO, @PathVariable Long id) {
        log.info("Request to update user with id: {}, to  update data: {}", id, updateDTO);
        var userDTO = userService.updateUser(updateDTO, id);
        log.info("User updated successfully: {}", userDTO);
        return userDTO;
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по указанному ID,"
                    + "операция доступна только администратору и пользователю чей ID совпадает с запрашиваемым")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @userUtils.checkCurrentUser(#id)")
    public void deleteUser(@PathVariable Long id) {
        log.info("Request to delete user with id: {}", id);
        userService.deleteUser(id);
        log.info("User delete successfully with id: {}", id);
    }
}
