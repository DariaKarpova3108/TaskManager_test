package com.example.app.component;

import com.example.app.models.Role;
import com.example.app.models.RoleName;
import com.example.app.repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        for (var roleName : RoleName.values()) {
            roleRepository.findByRoleName(roleName)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setRoleName(roleName);
                        return roleRepository.save(role);
                    });
        }
    }
}
