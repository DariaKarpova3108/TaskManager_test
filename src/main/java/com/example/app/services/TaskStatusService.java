package com.example.app.services;

import com.example.app.dto.taskStatus.TaskStatusCreateDTO;
import com.example.app.dto.taskStatus.TaskStatusDTO;
import com.example.app.dto.taskStatus.TaskStatusUpdateDTO;
import com.example.app.exception.LinkingTasksToAnotherEntityException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.mappers.TaskStatusMapper;
import com.example.app.repositories.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAllStatus() {
        return taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::map)
                .collect(Collectors.toList());
    }

    public TaskStatusDTO getStatus(Long id) {
        var model = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id: " + id + " not found"));
        return taskStatusMapper.map(model);
    }

    public TaskStatusDTO createStatus(TaskStatusCreateDTO createDTO) {
        if (taskStatusRepository.existsByName(createDTO.getStatusName())) {
            throw new IllegalArgumentException("A status with this name already exists");
        }
        var model = taskStatusMapper.map(createDTO);
        taskStatusRepository.save(model);
        return taskStatusMapper.map(model);
    }

    public TaskStatusDTO updateStatus(TaskStatusUpdateDTO updateDTO, Long id) {
        var model = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id: " + id + " not found"));
        taskStatusMapper.update(updateDTO, model);
        taskStatusRepository.save(model);
        return taskStatusMapper.map(model);
    }

    public void deleteStatus(Long id) {
        var model = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id: " + id + " not found"));
        if (!model.getListTasks().isEmpty()) {
            throw new LinkingTasksToAnotherEntityException("Task status cannot be deleted, they have assigned tasks");
        }
        taskStatusRepository.deleteById(id);
    }
}
