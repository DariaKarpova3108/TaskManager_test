package com.example.app.services;

import com.example.app.dto.user.UserCreateDTO;
import com.example.app.dto.user.UserDTO;
import com.example.app.dto.user.UserUpdateDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.mappers.UserMapper;
import com.example.app.models.RoleName;
import com.example.app.repositories.RoleRepository;
import com.example.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public List<UserDTO> getAllUsers() {
        var users = userRepository.findAll();
        log.info("Fetching all users, size list: {}", users.size());
        return users.stream()
                .map(userMapper::map)
                .collect(Collectors.toList());
    }

    public UserDTO getUser(Long id) {
        log.info("Fetching user with id: {}", id);
        var model = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with id: {} not found", id);
                    return new ResourceNotFoundException("User with id: " + id + " not found");
                });
        log.info("User with id: {} fetched successfully", id);
        return userMapper.map(model);
    }

    public UserDTO createUser(UserCreateDTO createDTO) {
        log.info("Creating a new user with email: {}", createDTO.getEmail());
        var model = userMapper.map(createDTO);
        var role = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> {
                    log.error("Default role USER not found");
                    return new ResourceNotFoundException("Default role USER not found");
                });
        model.getRoles().add(role);
        userRepository.save(model);
        log.info("User with email: {} created successfully", createDTO.getEmail());
        return userMapper.map(model);
    }

    public UserDTO updateUser(UserUpdateDTO updateDTO, Long id) {
        log.info("Updating user with id: {}", id);
        var model = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with id: {} not found", id);
                    return new ResourceNotFoundException("User with id: " + id + " not found");
                });
        userMapper.update(updateDTO, model);
        userRepository.save(model);
        log.info("User with id: {} updated successfully", id);
        return userMapper.map(model);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
        log.info("User with id: {} deleted successfully", id);
    }
}
