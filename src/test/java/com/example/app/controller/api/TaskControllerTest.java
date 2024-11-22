package com.example.app.controller.api;

import com.example.app.dto.task.TaskCreateDTO;
import com.example.app.dto.task.TaskDTO;
import com.example.app.dto.task.TaskUpdateDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.models.*;
import com.example.app.repositories.*;
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
public class TaskControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ModelGenerator modelGenerator;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository statusRepository;
    @Autowired
    private TaskPriorityRepository priorityRepository;
    @Autowired
    private RoleRepository roleRepository;
    private Task taskModel;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        var defaultRole = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User author = Instancio.of(modelGenerator.getUserModel()).create();
        author.setRoles(new HashSet<>(Set.of(defaultRole)));
        User assignee = Instancio.of(modelGenerator.getUserModel()).create();
        assignee.setRoles(new HashSet<>(Set.of(defaultRole)));
        userRepository.save(author);
        userRepository.save(assignee);

        TaskPriority priority = Instancio.of(modelGenerator.getPriorityModel()).create();
        priorityRepository.save(priority);

        TaskStatus status = Instancio.of(modelGenerator.getStatusModel()).create();
        statusRepository.save(status);

        taskModel = Instancio.of(modelGenerator.getTaskModel()).create();
        taskModel.setAuthor(author);
        taskModel.setAssignee(assignee);
        taskModel.setPriority(priority);
        taskModel.setStatus(status);
        taskRepository.save(taskModel);

        token = jwt().jwt(builder -> builder.subject(assignee.getEmail()));
    }

    @AfterEach
    public void cleanUp() {
        taskRepository.deleteAll();
        statusRepository.deleteAll();
        priorityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testGetListTasks() throws Exception {
        var request = get("/api/tasks");
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThat(body).isNotNull();
        assertThatJson(body).isArray();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testGetTask() throws Exception {
        var request = get("/api/tasks/" + taskModel.getId());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThat(body).isNotNull();
        assertThatJson(body)
                .and(n -> n.node("title").isEqualTo(taskModel.getTitle()))
                .and(n -> n.node("priority").isEqualTo(taskModel.getPriority().getPriorityName()))
                .and(n -> n.node("status").isEqualTo(taskModel.getStatus().getName()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateTask() throws Exception {
        var statusTask = statusRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        var priority = priorityRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found"));
        var author = userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var assignee = userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var createDTO = new TaskCreateDTO();
        createDTO.setTitle("title");
        createDTO.setDescription("description");
        createDTO.setStatus(statusTask.getName());
        createDTO.setPriority(priority.getPriorityName());
        createDTO.setAuthorId(author.getId());
        createDTO.setAssigneeId(assignee.getId());

        var request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).isNotNull();

        var savesTaskDTO = objectMapper.readValue(responseBody, TaskDTO.class);

        assertThat(savesTaskDTO).isNotNull();
        assertThat(savesTaskDTO.getTitle()).isEqualTo(createDTO.getTitle());
        assertThat(savesTaskDTO.getStatus()).isEqualTo(createDTO.getStatus());
    }

    @Test
    @Transactional
    public void testUpdateTask() throws Exception {
        var updateDTO = new TaskUpdateDTO();
        updateDTO.setTitle(JsonNullable.of("title2"));

        var request = put("/api/tasks/" + taskModel.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var updatedTask = taskRepository.findById(taskModel.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + taskModel.getId() + " not found"));

        assertThat(updatedTask.getTitle()).isEqualTo("title2");
        assertThat(updatedTask.getDescription()).isEqualTo(taskModel.getDescription());
        assertThat(updatedTask.getStatus().getName()).isEqualTo(taskModel.getStatus().getName());
        assertThat(updatedTask.getPriority().getPriorityName()).isEqualTo(taskModel.getPriority().getPriorityName());
        assertThat(updatedTask.getAuthor().getEmail()).isEqualTo(taskModel.getAuthor().getEmail());
    }

    @Test
    public void testDeleteTask() throws Exception {
        var request = delete("/api/tasks/" + taskModel.getId())
                .with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(taskModel.getId())).isEmpty();
    }
}
