package com.example.app.controller.api;

import com.example.app.exception.ResourceNotFoundException;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerTestWithParam {
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
    private TaskCommentRepository commentRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        var defaultRole = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User user = new User();
        user.setFirstName("Lili");
        user.setLastName("Smith");
        user.setEmail("test@test.com");
        user.setPasswordDigest("123");
        user.setRoles(new HashSet<>(Set.of(defaultRole)));
        userRepository.save(user);

        TaskPriority priority = new TaskPriority();
        priority.setPriorityName("High");
        priorityRepository.save(priority);

        TaskStatus status = new TaskStatus();
        status.setName("in_progress");
        statusRepository.save(status);

        TaskStatus status2 = new TaskStatus();
        status2.setName("completed");
        statusRepository.save(status2);

        Task taskModel = new Task();
        taskModel.setTitle("task_title1");
        taskModel.setDescription("task_description");
        taskModel.setAuthor(user);
        taskModel.setAssignee(user);
        taskModel.setPriority(priority);
        taskModel.setStatus(status);
        taskRepository.save(taskModel);

        Task taskModel2 = new Task();
        taskModel2.setTitle("task_title2");
        taskModel2.setDescription("task_description2");
        taskModel2.setAuthor(user);
        taskModel2.setAssignee(user);
        taskModel2.setPriority(priority);
        taskModel2.setStatus(status2);
        taskRepository.save(taskModel2);

        TaskComment commentModel = new TaskComment();
        commentModel.setAuthor(user);
        commentModel.setTask(taskModel);
        commentModel.setTitle("title_comment");
        commentModel.setDescription("comment_description");
        commentRepository.save(commentModel);

        TaskComment commentModel2 = new TaskComment();
        commentModel2.setAuthor(user);
        commentModel2.setTask(taskModel2);
        commentModel2.setTitle("title_comment");
        commentModel2.setDescription("comment_description");
        commentRepository.save(commentModel2);
    }

    @AfterEach
    public void cleanUp() {
        taskRepository.deleteAll();
        commentRepository.deleteAll();
        statusRepository.deleteAll();
        priorityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testGetListTasksWithAuthorIdAndAssigneeId() throws Exception {
        var request = get("/api/tasks?assigneeId=1&authorId=1");
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThat(body).isNotNull();
        assertThatJson(body).isArray();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testGetListTasksWithStatusCont() throws Exception {
        var request = get("/api/tasks?statusCont=completed");
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThat(body).isNotNull();
        assertThatJson(body).isArray().hasSize(1)
                .allSatisfy(element -> assertThatJson(element)
                        .and(n -> n.node("status").asString().containsIgnoringCase("completed")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testGetListTasksWithPriorityCont() throws Exception {
        var request = get("/api/tasks?priorityCont=High");
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThat(body).isNotNull();
        assertThatJson(body).isArray().hasSize(2)
                .allSatisfy(element -> assertThatJson(element)
                        .and(n -> n.node("priority").asString().containsIgnoringCase("High")));
    }
}
