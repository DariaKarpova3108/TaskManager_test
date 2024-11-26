package com.example.app.services;

import com.example.app.dto.taskStatus.TaskStatusCreateDTO;
import com.example.app.dto.taskStatus.TaskStatusDTO;
import com.example.app.dto.taskStatus.TaskStatusUpdateDTO;
import com.example.app.exception.LinkingTasksToAnotherEntityException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.mappers.TaskStatusMapper;
import com.example.app.repositories.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAllStatus() {
        var statuses = taskStatusRepository.findAll();
        log.info("Fetching all task statuses, with size: {}", statuses.size());
        return statuses.stream()
                .map(taskStatusMapper::map)
                .collect(Collectors.toList());
    }

    public TaskStatusDTO getStatus(Long id) {
        log.info("Fetching task status with id: {}", id);

        var model = taskStatusRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task status with id: {} not found", id);
                    return new ResourceNotFoundException("Task status with id: " + id + " not found");
                });

        log.info("Successfully fetched task status: {}", model.getName());
        return taskStatusMapper.map(model);
    }

    public TaskStatusDTO createStatus(TaskStatusCreateDTO createDTO) {
        log.info("Creating task status with name: {}", createDTO.getStatusName());

        if (taskStatusRepository.existsByName(createDTO.getStatusName())) {
            log.warn("A priority with the name {} already exists.", createDTO.getStatusName());
            throw new IllegalArgumentException("A status with this name already exists");
        }

        var model = taskStatusMapper.map(createDTO);
        taskStatusRepository.save(model);

        log.info("Task status '{}' created successfully", model.getName());
        return taskStatusMapper.map(model);
    }

    public TaskStatusDTO updateStatus(TaskStatusUpdateDTO updateDTO, Long id) {
        log.info("Updating task status with id: {}", id);

        var model = taskStatusRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task status with id: {} not found", id);
                    return new ResourceNotFoundException("Task status with id: " + id + " not found");
                });

        taskStatusMapper.update(updateDTO, model);
        taskStatusRepository.save(model);
        log.info("Task status with id: {} updated successfully", id);
        return taskStatusMapper.map(model);
    }

    public void deleteStatus(Long id) {
        log.info("Deleting task status with id: {}", id);

        var model = taskStatusRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task status with id: {} not found", id);
                   return new ResourceNotFoundException("Task status with id: " + id + " not found");
                });

        if (!model.getListTasks().isEmpty()) {
            log.error("Task status with id: {} cannot be deleted because it has assigned tasks", id);
            throw new LinkingTasksToAnotherEntityException("Task status cannot be deleted, they have assigned tasks");
        }

        taskStatusRepository.deleteById(id);
        log.info("Task status with id: {} deleted successfully", id);
    }
}
