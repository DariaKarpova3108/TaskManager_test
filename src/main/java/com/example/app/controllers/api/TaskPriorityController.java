package com.example.app.controllers.api;

import com.example.app.dto.taskPriority.TaskPriorityCreateDTO;
import com.example.app.dto.taskPriority.TaskPriorityDTO;
import com.example.app.dto.taskPriority.TaskPriorityUpdateDTO;
import com.example.app.services.TaskPriorityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/priority")
@RequiredArgsConstructor
public class TaskPriorityController {

    private final TaskPriorityService priorityService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TaskPriorityDTO>> getListPriority() {
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(priorityService.getAllPriority().size()))
                .body(priorityService.getAllPriority());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public TaskPriorityDTO getPriority(@PathVariable Long id) {
        return priorityService.getPriority(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskPriorityDTO createPriority(@RequestBody @Valid TaskPriorityCreateDTO createDTO) {
        return priorityService.createPriority(createDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskPriorityDTO updatePriority(@RequestBody @Valid TaskPriorityUpdateDTO updateDTO,
                                          @PathVariable Long id) {
        return priorityService.updatePriority(updateDTO, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePriority(@PathVariable Long id) {
        priorityService.deletePriority(id);
    }
}
