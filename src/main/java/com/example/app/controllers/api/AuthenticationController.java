package com.example.app.controllers.api;

import com.example.app.dto.AuthRequest;
import com.example.app.utils.JWTUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Контроллер для аутентификации",
        description = "Позволяет войти в систему после успешной проверки учетных данных")
public class AuthenticationController {
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Operation(
            summary = "Войти в систему",
            description = "Метод проверяет учетные данные пользователя и "
                    + "возвращает JWT токен в случае успешной аутентификации."
    )
    @PostMapping("/login")
    public String create(@RequestBody AuthRequest authRequest) {
        log.info("Received login request for email: {}", authRequest.getEmail());
        try {
            var authentication = new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(), authRequest.getPassword());

            authenticationManager.authenticate(authentication);
            log.info("Authentication successful for email: {}", authRequest.getEmail());

            var token = jwtUtils.generateToken(authRequest.getEmail());
            return token;
        } catch (BadCredentialsException ex) {
            log.error("Authentication failed for email: {}", authRequest.getEmail(), ex);
            throw new BadCredentialsException("Authentication failed", ex);
        }
    }
}
