package com.example.app.controllers.api;

import com.example.app.dto.taskStatus.TaskStatusCreateDTO;
import com.example.app.dto.taskStatus.TaskStatusDTO;
import com.example.app.dto.taskStatus.TaskStatusUpdateDTO;
import com.example.app.services.TaskStatusService;
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
@RequestMapping("/api/statuses")
@RequiredArgsConstructor
public class TaskStatusController {
    private final TaskStatusService statusService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TaskStatusDTO>> getListTasks() {
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(statusService.getAllStatus().size()))
                .body(statusService.getAllStatus());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public TaskStatusDTO getTaskStatus(@PathVariable Long id) {
        return statusService.getStatus(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskStatusDTO createStatus(@RequestBody @Valid TaskStatusCreateDTO createDTO) {
        return statusService.createStatus(createDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskStatusDTO updateStatus(@RequestBody @Valid TaskStatusUpdateDTO updateDTO, @PathVariable Long id) {
        return statusService.updateStatus(updateDTO, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStatus(@PathVariable Long id) {
        statusService.deleteStatus(id);
    }
}
