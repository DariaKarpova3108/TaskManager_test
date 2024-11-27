package com.example.app.component;

import com.example.app.dto.user.UserCreateDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.models.Role;
import com.example.app.models.RoleName;
import com.example.app.repositories.RoleRepository;
import com.example.app.repositories.UserRepository;
import com.example.app.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        log.info("Initializing data...");
        for (var roleName : RoleName.values()) {
            roleRepository.findByRoleName(roleName)
                    .ifPresentOrElse(
                            role -> log.info("Role {} already exists", roleName),
                            () -> {
                                Role role = new Role();
                                role.setRoleName(roleName);
                                roleRepository.save(role);
                                log.info("Saved role {}", roleName);
                            });
        }

        if (userService.getAllUsers().isEmpty()) {
            var user = new UserCreateDTO();
            user.setFirstName("Admin");
            user.setLastName("Admin");
            user.setPassword(passwordEncoder.encode("password"));
            user.setEmail("admin@example.com");
            userService.createUser(user);

            log.info("User with email {} was created", user.getEmail());

            var savedUser = userRepository.findByEmail("admin@example.com")
                    .orElseThrow(() -> {
                        log.error("User with email: {} not found", user.getEmail());
                        return new ResourceNotFoundException("User not found");
                    });

            var roleAdmin = roleRepository.findByRoleName(RoleName.ADMIN)
                    .orElseThrow(() -> new ResourceNotFoundException("Role ADMIN not found"));

            savedUser.getRoles().add(roleAdmin);
            userRepository.save(savedUser);

            log.info("Admin role assigned to user {}", savedUser.getEmail());
        }
    }
}
