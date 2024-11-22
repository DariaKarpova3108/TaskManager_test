package com.example.app.util;

import com.example.app.models.Task;
import com.example.app.models.TaskComment;
import com.example.app.models.TaskPriority;
import com.example.app.models.TaskStatus;
import com.example.app.models.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ModelGenerator {
    @Autowired
    private Faker faker;

    private Model<TaskStatus> statusModel;
    private Model<TaskPriority> priorityModel;
    private Model<User> userModel;
    private Model<TaskComment> commentModel;
    private Model<Task> taskModel;


    @PostConstruct
    private void init() {
        statusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> faker.name().title())
                .ignore(Select.field(TaskStatus::getListTasks))
                .toModel();

        priorityModel = Instancio.of(TaskPriority.class)
                .ignore(Select.field(TaskPriority::getId))
                .supply(Select.field(TaskPriority::getPriorityName),
                        () -> faker.lorem().word())
                .ignore(Select.field(TaskPriority::getListTasks))
                .toModel();

        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password())
                .ignore(Select.field(User::getRoles))
                .toModel();

        commentModel = Instancio.of(TaskComment.class)
                .ignore(Select.field(TaskComment::getId))
                .ignore(Select.field(TaskComment::getAuthor))
                .ignore(Select.field(TaskComment::getTask))
                .supply(Select.field(TaskComment::getTitle), () -> faker.name().title())
                .supply(Select.field(TaskComment::getDescription), () -> faker.lorem().paragraph())
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .supply(Select.field(Task::getTitle), () -> faker.name().title())
                .supply(Select.field(Task::getDescription), () -> faker.lorem().paragraph())
                .ignore(Select.field(Task::getTaskComments))
                .ignore(Select.field(Task::getPriority))
                .ignore(Select.field(Task::getAuthor))
                .ignore(Select.field(Task::getAssignee))
                .toModel();
    }
}
