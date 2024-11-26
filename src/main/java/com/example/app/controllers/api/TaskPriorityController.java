package com.example.app.controllers.api;

import com.example.app.dto.taskPriority.TaskPriorityCreateDTO;
import com.example.app.dto.taskPriority.TaskPriorityDTO;
import com.example.app.dto.taskPriority.TaskPriorityUpdateDTO;
import com.example.app.services.TaskPriorityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/api/priority")
@RequiredArgsConstructor
@Tag(name = "Контроллер приоритетов задач", description = "Позволяет проводить CRUD операции с приоритетами задач")
public class TaskPriorityController {
    private final TaskPriorityService priorityService;

    @Operation(
            summary = "Получить список приоритетов задач",
            description = "Возвращает список всех приоритетов задач")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TaskPriorityDTO>> getListPriority() {
        log.info("Fetching all task priorities");
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(priorityService.getAllPriority().size()))
                .body(priorityService.getAllPriority());
    }

    @Operation(
            summary = "Получить приоритет задачи по ID",
            description = "Возвращает приоритет задачи по указанному ID")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public TaskPriorityDTO getPriority(@PathVariable Long id) {
        log.info("Fetching task priority with ID: {}", id);
        return priorityService.getPriority(id);
    }

    @Operation(
            summary = "Создать новый приоритет задачи",
            description = "Создает новый приоритет задачи, операция доступна пользователям с ролью администратор")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskPriorityDTO createPriority(@RequestBody @Valid TaskPriorityCreateDTO createDTO) {
        log.info("Request to create task priority: {}", createDTO);
        var priorityDTO = priorityService.createPriority(createDTO);
        log.info("Task priority created successfully: {}", priorityDTO);
        return priorityDTO;
    }

    @Operation(
            summary = "Обновить приоритет задачи",
            description = "Обновляет приоритет задачи по указанному ID, "
                    + "операция доступна пользователям с ролью администратор")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskPriorityDTO updatePriority(@RequestBody @Valid TaskPriorityUpdateDTO updateDTO,
                                          @PathVariable Long id) {
        log.info("Request to update task priority with id: {}, to  update data: {}", id, updateDTO);
        var priorityDTO = priorityService.updatePriority(updateDTO, id);
        log.info("Task priority updated successfully: {}", priorityDTO);
        return priorityDTO;
    }

    @Operation(
            summary = "Удалить приоритет задачи",
            description = "Удаляет приоритет задачи по указанному ID, "
                    + "операция доступна пользователям с ролью администратор")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePriority(@PathVariable Long id) {
        log.info("Request to delete task priority with id: {}", id);
        priorityService.deletePriority(id);
        log.info("Task priority delete successfully with id: {}", id);
    }
}
