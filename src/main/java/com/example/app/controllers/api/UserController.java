package com.example.app.controllers.api;

import com.example.app.dto.user.UserCreateDTO;
import com.example.app.dto.user.UserDTO;
import com.example.app.dto.user.UserUpdateDTO;
import com.example.app.services.UserService;
import com.example.app.utils.UserUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserUtils userUtils;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<UserDTO>> getListUsers() {
        return ResponseEntity.ok()
                .header("X-TotalCount", String.valueOf(userService.getAllUsers().size()))
                .body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public UserDTO createUser(@RequestBody @Valid UserCreateDTO createDTO) {
        return userService.createUser(createDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @userUtils.checkCurrentUser(#id)")
    public UserDTO updateUser(@RequestBody @Valid UserUpdateDTO updateDTO, @PathVariable Long id) {
        return userService.updateUser(updateDTO, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @userUtils.checkCurrentUser(#id)")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
