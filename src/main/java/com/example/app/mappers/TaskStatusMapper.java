package com.example.app.mappers;

import com.example.app.dto.taskStatus.TaskStatusCreateDTO;
import com.example.app.dto.taskStatus.TaskStatusDTO;
import com.example.app.dto.taskStatus.TaskStatusUpdateDTO;
import com.example.app.models.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
public abstract class TaskStatusMapper {
    @Mapping(target = "name", source = "statusName")
    public abstract TaskStatus map(TaskStatusCreateDTO createDTO);

    @Mapping(target = "statusName", source = "name")
    public abstract TaskStatusDTO map(TaskStatus model);
    @Mapping(target = "name", source = "statusName")
    public abstract void update(TaskStatusUpdateDTO updateDTO, @MappingTarget TaskStatus model);
}
