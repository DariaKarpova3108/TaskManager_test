package com.example.app.controller.api;

import com.example.app.dto.taskPriority.TaskPriorityCreateDTO;
import com.example.app.dto.taskPriority.TaskPriorityUpdateDTO;
import com.example.app.models.TaskPriority;
import com.example.app.repositories.TaskPriorityRepository;
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
public class TaskPriorityControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TaskPriorityRepository priorityRepository;
    @Autowired
    private ModelGenerator modelGenerator;
    private TaskPriority priorityModel;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        priorityModel = Instancio.of(modelGenerator.getPriorityModel()).create();
        priorityRepository.save(priorityModel);
    }

    @AfterEach
    public void cleanUp() {
        if (!priorityRepository.findAll().isEmpty()) {
            priorityRepository.deleteAll();
        }
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testGetListPriority() throws Exception {
        var request = get("/api/priority");
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThat(body).isNotNull();
        assertThatJson(body).isArray();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testGetPriority() throws Exception {
        var request = get("/api/priority/" + priorityModel.getId());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThat(body).isNotNull();
        assertThatJson(body)
                .and(n -> n.node("priority_name").isEqualTo(priorityModel.getPriorityName()));
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreatePriority() throws Exception {
        var createDTO = new TaskPriorityCreateDTO();
        createDTO.setPriorityName("test");

        var request = post("/api/priority")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var savedPriority = priorityRepository.findByPriorityName("test");

        assertThat(savedPriority).isPresent();
        assertThat(savedPriority.get().getPriorityName()).isEqualTo("test");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdatePriority() throws Exception {
        var updateDTO = new TaskPriorityUpdateDTO();
        updateDTO.setPriorityName(JsonNullable.of("test2"));

        var request = put("/api/priority/" + priorityModel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var updatedPriority = priorityRepository.findByPriorityName("test2");

        assertThat(updatedPriority).isPresent();
        assertThat(updatedPriority.get().getPriorityName()).isEqualTo("test2");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeletePriority() throws Exception {
        var request = delete("/api/priority/" + priorityModel.getId());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(priorityRepository.findById(priorityModel.getId())).isEmpty();
    }
}
