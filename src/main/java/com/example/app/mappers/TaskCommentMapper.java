package com.example.app.mappers;

import com.example.app.dto.taskComment.TaskCommentCreateDTO;
import com.example.app.dto.taskComment.TaskCommentDTO;
import com.example.app.dto.taskComment.TaskCommentUpdateDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.models.TaskComment;
import com.example.app.models.User;
import com.example.app.repositories.UserRepository;
import org.mapstruct.*;
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

    @Mapping(target = "author", source = "authorId", qualifiedByName = "findUser")
    public abstract TaskComment map(TaskCommentCreateDTO createDTO);

    @Mapping(target = "authorId", source = "author.id")
    public abstract TaskCommentDTO map(TaskComment model);

    @Mapping(target = "author", source = "authorId", qualifiedByName = "findUser")
    public abstract void update(TaskCommentUpdateDTO updateDTO, @MappingTarget TaskComment model);

    @Named("findUser")
    public User findUser(Long authorId) {
        return userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + authorId + " not found"));
    }
}
