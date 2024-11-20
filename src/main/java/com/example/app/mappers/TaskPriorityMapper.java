package com.example.app.mappers;

import com.example.app.dto.taskPriority.TaskPriorityCreateDTO;
import com.example.app.dto.taskPriority.TaskPriorityDTO;
import com.example.app.dto.taskPriority.TaskPriorityUpdateDTO;
import com.example.app.models.TaskPriority;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskPriorityMapper {
    public abstract TaskPriority map(TaskPriorityCreateDTO createDTO);
    public abstract TaskPriorityDTO map(TaskPriority model);
    public abstract void update(TaskPriorityUpdateDTO updateDTO, @MappingTarget TaskPriority model);
}
