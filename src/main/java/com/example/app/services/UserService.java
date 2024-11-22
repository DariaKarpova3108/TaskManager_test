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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::map)
                .collect(Collectors.toList());
    }

    public UserDTO getUser(Long id) {
        var model = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
        return userMapper.map(model);
    }

    public UserDTO createUser(UserCreateDTO createDTO) {
        var model = userMapper.map(createDTO);
        var role = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role USER not found"));
        model.getRoles().add(role);
        userRepository.save(model);
        return userMapper.map(model);
    }

    public UserDTO updateUser(UserUpdateDTO updateDTO, Long id) {
        var model = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
        userMapper.update(updateDTO, model);
        userRepository.save(model);
        return userMapper.map(model);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
