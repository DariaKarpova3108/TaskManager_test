package com.example.app.mappers;

import com.example.app.dto.taskComment.TaskCommentCreateDTO;
import com.example.app.dto.taskComment.TaskCommentDTO;
import com.example.app.dto.taskComment.TaskCommentUpdateDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.models.Task;
import com.example.app.models.TaskComment;
import com.example.app.models.User;
import com.example.app.repositories.TaskRepository;
import com.example.app.repositories.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskCommentMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Mapping(target = "author", source = "authorId", qualifiedByName = "findUser")
    @Mapping(target = "task", source = "taskId", qualifiedByName = "findTask")
    public abstract TaskComment map(TaskCommentCreateDTO createDTO);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "taskId", source = "task.id")
    public abstract TaskCommentDTO map(TaskComment model);

    @Mapping(target = "author", source = "authorId", qualifiedByName = "findUser")
    @Mapping(target = "task", source = "taskId", qualifiedByName = "findTask")
    public abstract void update(TaskCommentUpdateDTO updateDTO, @MappingTarget TaskComment model);

    @Named("findUser")
    public User findUser(Long authorId) {
        return userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + authorId + " not found"));
    }

    @Named("findTask")
    public Task findTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + taskId + " not found"));
    }
}
