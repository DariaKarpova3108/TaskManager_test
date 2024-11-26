package com.example.app.services;

import com.example.app.dto.taskPriority.TaskPriorityCreateDTO;
import com.example.app.dto.taskPriority.TaskPriorityDTO;
import com.example.app.dto.taskPriority.TaskPriorityUpdateDTO;
import com.example.app.exception.LinkingTasksToAnotherEntityException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.mappers.TaskPriorityMapper;
import com.example.app.repositories.TaskPriorityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskPriorityService {
    private final TaskPriorityRepository priorityRepository;
    private final TaskPriorityMapper priorityMapper;

    public List<TaskPriorityDTO> getAllPriority() {
        var priorities = priorityRepository.findAll();
        log.info("Fetching all task priorities, size: {} task priorities", priorities.size());
        return priorities.stream()
                .map(priorityMapper::map)
                .collect(Collectors.toList());
    }

    public TaskPriorityDTO getPriority(Long id) {
        log.info("Fetching task priority with id: {}", id);

        var model = priorityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task priority with id: {} not found.", id);
                    return new ResourceNotFoundException("Task priority with id: " + id + " not found");
                });

        log.info("Successfully fetched task priority with id: {}", id);
        return priorityMapper.map(model);
    }

    public TaskPriorityDTO createPriority(TaskPriorityCreateDTO createDTO) {
        log.info("Creating task priority with name: {}", createDTO.getPriorityName());

        if (priorityRepository.existsByPriorityName(createDTO.getPriorityName())) {
            log.warn("A priority with the name {} already exists.", createDTO.getPriorityName());
            throw new IllegalArgumentException("A priority with this name already exists");
        }

        var model = priorityMapper.map(createDTO);
        priorityRepository.save(model);
        log.info("Successfully created task priority with name: {}", createDTO.getPriorityName());
        return priorityMapper.map(model);
    }

    public TaskPriorityDTO updatePriority(TaskPriorityUpdateDTO updateDTO, Long id) {
        log.info("Updating task priority with id: {}", id);

        var model = priorityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task priority with id: {} not found.", id);
                    return new ResourceNotFoundException("Task priority with id: " + id + " not found");
                });

        priorityMapper.update(updateDTO, model);
        priorityRepository.save(model);
        log.info("Successfully updated task priority with id: {}", id);
        return priorityMapper.map(model);
    }

    public void deletePriority(Long id) {
        log.info("Deleting task priority with id: {}", id);

        var model = priorityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task priority with id: {} not found.", id);
                    return new ResourceNotFoundException("Task priority with id: " + id + " not found");
                });

        if (!model.getListTasks().isEmpty()) {
            log.warn("Cannot delete task priority with id: {} because it has assigned tasks", id);
            throw new LinkingTasksToAnotherEntityException("Task priority cannot be deleted, they have assigned tasks");
        }
        priorityRepository.deleteById(id);
        log.info("Successfully deleted task priority with id: {}", id);
    }
}
