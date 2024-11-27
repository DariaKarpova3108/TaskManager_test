package com.example.app.controller.api;

import com.example.app.dto.user.UserCreateDTO;
import com.example.app.dto.user.UserUpdateDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.mappers.RoleMapper;
import com.example.app.models.Role;
import com.example.app.models.RoleName;
import com.example.app.models.User;
import com.example.app.repositories.RoleRepository;
import com.example.app.repositories.UserRepository;
import com.example.app.util.ModelGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelGenerator modelGenerator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleMapper roleMapper;
    private User userModel;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        userModel = Instancio.of(modelGenerator.getUserModel()).create();
        userModel.setEmail("user@example.com");

        Role role = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        userModel.setRoles(new HashSet<>(Set.of(role)));
        userRepository.save(userModel);

        assertThat(userRepository.findByEmail(userModel.getEmail())).isPresent();

        token = jwt().jwt(builder -> builder.subject(userModel.getEmail()));
    }

    @AfterEach
    public void cleanUp() {
        if (!userRepository.findAll().isEmpty()) {
            userRepository.deleteAll();
        }
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testGetListUsers() throws Exception {
        var request = get("/api/users");
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThat(body).isNotNull();
        assertThatJson(body).isArray();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testGetUser() throws Exception {
        var request = get("/api/users/" + userModel.getId());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThat(body).isNotNull();
        assertThatJson(body)
                .and(n -> n.node("first_name").isEqualTo(userModel.getFirstName()))
                .and(n -> n.node("last_name").isEqualTo(userModel.getLastName()))
                .and(n -> n.node("email").isEqualTo(userModel.getEmail()));
    }

    @Test
    @Transactional
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testCreateUser() throws Exception {
        var createDTO = new UserCreateDTO();
        createDTO.setFirstName("lili");
        createDTO.setLastName("smith");
        createDTO.setEmail("test@test.com");
        createDTO.setPassword("qwerty");

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var savedUser = userRepository.findByEmail("test@test.com");

        assertThat(savedUser).isPresent();

        assertThat(savedUser.get().getRoles()).extracting("roleName").containsExactly(RoleName.USER);
        assertThat(savedUser.get().getFirstName()).isEqualTo(createDTO.getFirstName());
        assertThat(savedUser.get().getLastName()).isEqualTo(createDTO.getLastName());
        assertThat(savedUser.get().getEmail()).isEqualTo(createDTO.getEmail());
    }

    @Test
    @Transactional
  //  @WithMockUser(roles = {"ADMIN"})
    public void testUpdateUser() throws Exception {
        var roleAdmin = roleRepository.findByRoleName(RoleName.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role ADMIN not found"));

        var roleUser = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role USER not found"));

        var updateDTO = new UserUpdateDTO();
        updateDTO.setRoles(JsonNullable.of(new HashSet<>(Set.of(roleMapper.map(roleAdmin), roleMapper.map(roleUser)))));
        updateDTO.setFirstName(JsonNullable.of("newName"));


        var request = put("/api/users/" + userModel.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var updatedUser = userRepository.findById(userModel.getId());

        assertThat(updatedUser).isPresent();

        assertThat(updatedUser.get().getFirstName()).isEqualTo(updateDTO.getFirstName().get());
        assertThat(updatedUser.get().getRoles().size()).isEqualTo(2);
    }

    @Test
  //  @WithMockUser(roles = {"ADMIN"})
    public void testDeleteUser() throws Exception {
        var request = delete("/api/users/" + userModel.getId())
                .with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(userModel.getId())).isEmpty();
    }
}
