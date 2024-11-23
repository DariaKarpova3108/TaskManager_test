package com.example.app.services;

import com.example.app.dto.task.TaskCreateDTO;
import com.example.app.dto.task.TaskDTO;
import com.example.app.dto.task.TaskUpdateDTO;
import com.example.app.dto.task.TaskUpdateForAssigneeDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.mappers.TaskMapper;
import com.example.app.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public List<TaskDTO> getAllTask() {
        return taskRepository.findAll().stream()
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
}
