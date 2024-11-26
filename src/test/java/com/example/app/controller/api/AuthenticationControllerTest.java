package com.example.app.controller.api;

import com.example.app.dto.AuthRequest;
import com.example.app.utils.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JWTUtils jwtUtils;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    public void shouldReturnTokenWhenLoginIsSuccessful() throws Exception {
        AuthRequest personData = new AuthRequest();
        personData.setEmail("test@example.com");
        personData.setPassword("123");

        var token = "mocked-jwt-token";

        Mockito.when(jwtUtils.generateToken(personData.getEmail())).thenReturn(token);
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(personData.getEmail(),
                        personData.getPassword()));

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personData));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(token));
    }

    @Test
    public void shouldReturnTokenWhenLoginIsFail() throws Exception {
        AuthRequest personData = new AuthRequest();
        personData.setEmail("test@example.com");
        personData.setPassword("wrong-password");

        Mockito.doThrow(new BadCredentialsException("Invalid email or password"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personData));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
