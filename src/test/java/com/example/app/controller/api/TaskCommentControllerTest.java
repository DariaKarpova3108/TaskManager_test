package com.example.app.controller.api;

import com.example.app.dto.taskComment.TaskCommentCreateDTO;
import com.example.app.dto.taskComment.TaskCommentDTO;
import com.example.app.dto.taskComment.TaskCommentUpdateDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.models.Role;
import com.example.app.models.RoleName;
import com.example.app.models.Task;
import com.example.app.models.TaskComment;
import com.example.app.models.TaskPriority;
import com.example.app.models.TaskStatus;
import com.example.app.models.User;
import com.example.app.repositories.RoleRepository;
import com.example.app.repositories.TaskCommentRepository;
import com.example.app.repositories.TaskPriorityRepository;
import com.example.app.repositories.TaskRepository;
import com.example.app.repositories.TaskStatusRepository;
import com.example.app.repositories.UserRepository;
import com.example.app.util.ModelGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskCommentControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TaskCommentRepository commentRepository;
    @Autowired
    private ModelGenerator modelGenerator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TaskStatusRepository statusRepository;
    @Autowired
    private TaskPriorityRepository priorityRepository;
    @Autowired
    private TaskRepository taskRepository;
    private TaskComment commentModel;
    private User userModel;
    private Task taskModel;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        Role defaultRole = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        userModel = Instancio.of(modelGenerator.getUserModel()).create();
        userModel.setRoles(new HashSet<>(Set.of(defaultRole)));
        userRepository.save(userModel);

        TaskPriority priority = Instancio.of(modelGenerator.getPriorityModel()).create();
        priorityRepository.save(priority);

        TaskStatus status = Instancio.of(modelGenerator.getStatusModel()).create();
        statusRepository.save(status);

        taskModel = Instancio.of(modelGenerator.getTaskModel()).create();
        taskModel.setAuthor(userModel);
        taskModel.setAssignee(userModel);
        taskModel.setPriority(priority);
        taskModel.setStatus(status);
        taskRepository.save(taskModel);

        commentModel = Instancio.of(modelGenerator.getCommentModel()).create();
        commentModel.setAuthor(userModel);
        commentModel.setTask(taskModel);
        commentRepository.save(commentModel);

        token = jwt().jwt(builder -> builder.subject(userModel.getEmail()));
    }


    @Test
    @Transactional
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testGetListTaskComments() throws Exception {
        var request = get("/api/tasks/" + taskModel.getId() + "/comments");
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThat(body).isNotNull();
        assertThatJson(body).isArray();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    @Transactional
    public void testGetTaskComment() throws Exception {
        var request = get("/api/tasks/" + taskModel.getId() + "/comments/" + commentModel.getId());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThat(body).isNotNull();
        assertThatJson(body)
                .and(n -> n.node("title").isEqualTo(commentModel.getTitle()));
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Transactional
    public void testCreateTaskComment() throws Exception {
        var createDTO = new TaskCommentCreateDTO();
        createDTO.setTitle("newTitle");
        createDTO.setAuthorId(userModel.getId()); //????
        createDTO.setDescription("text");

        var request = post("/api/tasks/" + taskModel.getId() + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).isNotNull();

        var savedCommentDTO = objectMapper.readValue(responseBody, TaskCommentDTO.class);

        assertThat(savedCommentDTO).isNotNull();
        assertThat(savedCommentDTO.getTitle()).isEqualTo("newTitle");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Transactional
    public void testUpdateTaskComment() throws Exception {
        var updateDTO = new TaskCommentUpdateDTO();
        updateDTO.setTitle(JsonNullable.of("updatedTitle"));

        var request = put("/api/tasks/" + taskModel.getId() + "/comments/" + commentModel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).isNotNull();

        var updatedCommentDTO = objectMapper.readValue(responseBody, TaskCommentDTO.class);

        assertThat(updatedCommentDTO.getTitle()).isEqualTo("updatedTitle");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Transactional
    public void testDeleteTaskComment() throws Exception {
        var request = delete("/api/tasks/" + taskModel.getId() + "/comments/" + commentModel.getId());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(commentRepository.findById(commentModel.getId())).isEmpty();
    }
}
