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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;

    public List<TaskDTO> getAllTask(TaskParamDTO paramDTO, int page, String sort) {

        String[] sortParams = sort.split(",");

        if (sortParams.length != 2 || (!sortParams[1].equals("asc") && !sortParams[1].equals("desc"))) {
            throw new IllegalArgumentException("Invalid sort direction");
        }

        Sort sortOrder = Sort.by(
                sortParams[1].equals("asc") ? Sort.Order.asc(sortParams[0].trim())
                        : Sort.Order.desc(sortParams[0].trim())
        );

        Specification<Task> specification = taskSpecification.build(paramDTO, sortOrder);
        Pageable pageable = PageRequest.of(page - 1, 10, sortOrder);

        return taskRepository.findAll(specification, pageable)
                .stream()
                .map(taskMapper::map)
                .collect(Collectors.toList());
    }

    public TaskDTO getTask(Long id) {
        var model = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));
        return taskMapper.map(model);
    }

    public TaskDTO createTask(TaskCreateDTO createDTO) {
        var model = taskMapper.map(createDTO);
        taskRepository.save(model);
        return taskMapper.map(model);
    }

    public TaskDTO updateTask(TaskUpdateDTO updateDTO, Long id) {
        var model = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));
        taskMapper.update(updateDTO, model);
        taskRepository.save(model);
        return taskMapper.map(model);
    }

    public TaskDTO updateTaskForAssignee(TaskUpdateForAssigneeDTO updateForAssigneeDTO, Long id) {
        var model = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));
        taskMapper.updateForAssignee(updateForAssigneeDTO, model);
        taskRepository.save(model);
        return taskMapper.map(model);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public long totalCountListOfTask(TaskParamDTO paramDTO) {
        Specification<Task> specification = taskSpecification.build(paramDTO, null);
        return taskRepository.count(specification);
    }
}
