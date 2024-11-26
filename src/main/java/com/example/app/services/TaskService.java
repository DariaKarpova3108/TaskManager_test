package com.example.app.services;

import com.example.app.dto.specificationDTO.TaskParamDTO;
import com.example.app.dto.task.TaskCreateDTO;
import com.example.app.dto.task.TaskDTO;
import com.example.app.dto.task.TaskUpdateDTO;
import com.example.app.dto.task.TaskUpdateForAssigneeDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.mappers.TaskMapper;
import com.example.app.models.Task;
import com.example.app.repositories.TaskRepository;
import com.example.app.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;

    public List<TaskDTO> getAllTask(TaskParamDTO paramDTO, int page, String sort) {
        log.info("Fetching tasks with parameters: {}, page: {}, sort: {}", paramDTO, page, sort);

        String[] sortParams = sort.split(",");

        if (sortParams.length != 2 || (!sortParams[1].equals("asc") && !sortParams[1].equals("desc"))) {
            log.error("Invalid sort direction provided: {}", sort);
            throw new IllegalArgumentException("Invalid sort direction");
        }

        Sort sortOrder = Sort.by(
                sortParams[1].equals("asc") ? Sort.Order.asc(sortParams[0].trim())
                        : Sort.Order.desc(sortParams[0].trim())
        );

        Specification<Task> specification = taskSpecification.build(paramDTO, sortOrder);
        Pageable pageable = PageRequest.of(page - 1, 10, sortOrder);

        log.info("Successfully fetched tasks, total size: {}",
                taskRepository.findAll(specification, pageable).getTotalElements());

        return taskRepository.findAll(specification, pageable)
                .stream()
                .map(taskMapper::map)
                .collect(Collectors.toList());
    }

    public TaskDTO getTask(Long id) {
        log.info("Fetching task with id: {}", id);

        var model = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task with id: {} not found", id);
                    return new ResourceNotFoundException("Task with id: " + id + " not found");
                });

        log.info("Successfully fetched task with id: {}", id);
        return taskMapper.map(model);
    }

    public TaskDTO createTask(TaskCreateDTO createDTO) {
        log.info("Creating task with details: {}", createDTO);
        var model = taskMapper.map(createDTO);
        taskRepository.save(model);
        log.info("Task created successfully with id: {}", model.getId());
        return taskMapper.map(model);
    }

    public TaskDTO updateTask(TaskUpdateDTO updateDTO, Long id) {
        log.info("Updating task with id: {}, new data: {}", id, updateDTO);

        var model = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task with id: {} not found", id);
                    return new ResourceNotFoundException("Task with id: " + id + " not found");
                });

        taskMapper.update(updateDTO, model);
        taskRepository.save(model);
        log.info("Task with id: {} updated successfully", id);
        return taskMapper.map(model);
    }

    public TaskDTO updateTaskForAssignee(TaskUpdateForAssigneeDTO updateForAssigneeDTO, Long id) {
        log.info("Updating assignee for task with id: {}, new assignee data: {}", id, updateForAssigneeDTO);

        var model = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task with id: {} not found", id);
                    return new ResourceNotFoundException("Task with id: " + id + " not found");
                });

        taskMapper.updateForAssignee(updateForAssigneeDTO, model);
        taskRepository.save(model);
        log.info("Assignee for task with id: {} updated successfully", id);
        return taskMapper.map(model);
    }

    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
        log.info("Task with id: {} deleted successfully", id);
    }

    public long totalCountListOfTask(TaskParamDTO paramDTO) {
        log.info("Counting total tasks with parameters: {}", paramDTO);
        Specification<Task> specification = taskSpecification.build(paramDTO, null);
        return taskRepository.count(specification);
    }
}
