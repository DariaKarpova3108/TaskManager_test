package com.example.app.controller.api;

import com.example.app.dto.taskStatus.TaskStatusCreateDTO;
import com.example.app.dto.taskStatus.TaskStatusUpdateDTO;
import com.example.app.models.TaskStatus;
import com.example.app.repositories.TaskStatusRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TaskStatusControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskStatusRepository statusRepository;
    @Autowired
    private ModelGenerator modelGenerator;
    private TaskStatus taskStatusModel;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        taskStatusModel = Instancio.of(modelGenerator.getStatusModel()).create();
        statusRepository.save(taskStatusModel);
    }

    @AfterEach
    public void cleanUp() {
        if (!statusRepository.findAll().isEmpty()) {
            statusRepository.deleteAll();
        }
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testGetListStatuses() throws Exception {
        var request = get("/api/statuses");
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThat(body).isNotNull();
        assertThatJson(body).isArray();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testGetStatus() throws Exception {
        var request = get("/api/statuses/" + taskStatusModel.getId());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThat(body).isNotNull();
        assertThatJson(body)
                .and(n -> n.node("status_name").isEqualTo(taskStatusModel.getName()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateStatus() throws Exception {
        var createDTO = new TaskStatusCreateDTO();
        createDTO.setStatusName("newStatus");

        var request = post("/api/statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var savedStatus = statusRepository.findByName("newStatus");

        assertThat(savedStatus).isPresent();
        assertThat(savedStatus.get().getName()).isEqualTo("newStatus");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateStatus() throws Exception {
        var updateDTO = new TaskStatusUpdateDTO();
        updateDTO.setStatusName(JsonNullable.of("status2"));

        var request = put("/api/statuses/" + taskStatusModel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var updatedStatus = statusRepository.findByName("status2");

        assertThat(updatedStatus).isPresent();
        assertThat(updatedStatus.get().getName()).isEqualTo("status2");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteStatus() throws Exception {
        var request = delete("/api/statuses/" + taskStatusModel.getId());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(statusRepository.findById(taskStatusModel.getId())).isEmpty();
    }
}
