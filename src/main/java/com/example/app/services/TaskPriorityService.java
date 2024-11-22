package com.example.app.services;

import com.example.app.dto.taskPriority.TaskPriorityCreateDTO;
import com.example.app.dto.taskPriority.TaskPriorityDTO;
import com.example.app.dto.taskPriority.TaskPriorityUpdateDTO;
import com.example.app.exception.LinkingTasksToAnotherEntityException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.mappers.TaskPriorityMapper;
import com.example.app.repositories.TaskPriorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskPriorityService {
    private final TaskPriorityRepository priorityRepository;
    private final TaskPriorityMapper priorityMapper;

    public List<TaskPriorityDTO> getAllPriority() {
        return priorityRepository.findAll().stream()
                .map(priorityMapper::map)
                .collect(Collectors.toList());
    }

    public TaskPriorityDTO getPriority(Long id) {
        var model = priorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task priority with id: " + id + " not found"));
        return priorityMapper.map(model);
    }

    public TaskPriorityDTO createPriority(TaskPriorityCreateDTO createDTO) {
        if (priorityRepository.existsByPriorityName(createDTO.getPriorityName())) {
            throw new IllegalArgumentException("A priority with this name already exists");
        }
        var model = priorityMapper.map(createDTO);
        priorityRepository.save(model);
        return priorityMapper.map(model);
    }

    public TaskPriorityDTO updatePriority(TaskPriorityUpdateDTO updateDTO, Long id) {
        var model = priorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task priority with id: " + id + " not found"));
        priorityMapper.update(updateDTO, model);
        priorityRepository.save(model);
        return priorityMapper.map(model);
    }

    public void deletePriority(Long id) {
        var model = priorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task priority with id: " + id + " not found"));

        if (!model.getListTasks().isEmpty()) {
            throw new LinkingTasksToAnotherEntityException("Task priority cannot be deleted, they have assigned tasks");
        }

        priorityRepository.deleteById(id);
    }
}
