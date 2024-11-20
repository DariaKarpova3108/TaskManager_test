package com.example.app.mappers;

import com.example.app.dto.task.TaskCreateDTO;
import com.example.app.dto.task.TaskDTO;
import com.example.app.dto.task.TaskUpdateDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.models.Task;
import com.example.app.models.TaskComment;
import com.example.app.models.TaskPriority;
import com.example.app.models.TaskStatus;
import com.example.app.models.User;
import com.example.app.repositories.TaskCommentRepository;
import com.example.app.repositories.TaskPriorityRepository;
import com.example.app.repositories.TaskStatusRepository;
import com.example.app.repositories.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class, TaskCommentMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class TaskMapper {
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskPriorityRepository taskPriorityRepository;
    @Autowired
    private TaskCommentRepository taskCommentRepository;
    @Autowired
    private UserRepository userRepository;


    @Mapping(target = "status", source = "status", qualifiedByName = "toModelTaskStatus")
    @Mapping(target = "priority", source = "priority", qualifiedByName = "toModelTaskPriority")
    @Mapping(target = "author", source = "authorId", qualifiedByName = "findUserById")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "findUserById")
    public abstract Task map(TaskCreateDTO createDTO);

    @Mapping(target = "status", source = "status.name")
    @Mapping(target = "priority", source = "priority.priorityName")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "assigneeId", source = "assignee.id")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "status", source = "status", qualifiedByName = "toModelTaskStatus")
    @Mapping(target = "priority", source = "priority", qualifiedByName = "toModelTaskPriority")
    @Mapping(target = "taskComments", source = "taskCommentsId", qualifiedByName = "toModelTaskComments")
    @Mapping(target = "author", source = "authorId", qualifiedByName = "findUserById")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "findUserById")
    public abstract void update(TaskUpdateDTO updateDTO, @MappingTarget Task model);

    @Named("toModelTaskStatus")
    public TaskStatus toModelTaskStatus(String status) {
        return taskStatusRepository.findByName(status)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with name: " + status + " not found"));
    }

    @Named("toModelTaskPriority")
    public TaskPriority toModelTaskPriority(String priority) {
        return taskPriorityRepository.findByPriorityName(priority)
                .orElseThrow(() -> new ResourceNotFoundException("Task priority with name: " + priority + " not found"));
    }

    @Named("findUserById")
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
    }

    @Named("toModelTaskComments")
    public List<TaskComment> toModelTaskComments(List<Long> taskCommentsId) {
        return taskCommentsId.stream()
                .distinct()
                .map(id -> taskCommentRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("task comment with id: " + id + " not found")))
                .collect(Collectors.toList());
    }
}
