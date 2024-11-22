package com.example.app.utils;

import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserUtils {
    private final UserRepository userRepository;

    public boolean checkCurrentUser(Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        var email = authentication.getName();

        var currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + " not found"));
        boolean result = currentUser.getId().equals(id);
        return result;
    }
}

