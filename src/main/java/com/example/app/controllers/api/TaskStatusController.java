package com.example.app.controllers.api;

import com.example.app.dto.taskStatus.TaskStatusCreateDTO;
import com.example.app.dto.taskStatus.TaskStatusDTO;
import com.example.app.dto.taskStatus.TaskStatusUpdateDTO;
import com.example.app.services.TaskStatusService;
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
@RequestMapping("/api/statuses")
@RequiredArgsConstructor
@Tag(name = "Контроллер статусов задач", description = "Позволяет проводить CRUD операции со статусами")
public class TaskStatusController {
    private final TaskStatusService statusService;

    @Operation(
            summary = "Получить список статусов задач",
            description = "Возвращает список всех статусов задач")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TaskStatusDTO>> getListTasksStatus() {
        log.info("Fetching all task statuses");
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(statusService.getAllStatus().size()))
                .body(statusService.getAllStatus());
    }

    @Operation(
            summary = "Получить статус задачи по ID",
            description = "Возвращает статус задачи по указанному ID")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public TaskStatusDTO getTaskStatus(@PathVariable Long id) {
        log.info("Fetching task status with ID: {}", id);
        return statusService.getStatus(id);
    }

    @Operation(
            summary = "Создать новый статус задачи",
            description = "Создает новый статус задачи, "
                    + "операция доступна пользователям с ролью администратор")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskStatusDTO createTaskStatus(@RequestBody @Valid TaskStatusCreateDTO createDTO) {
        log.info("Creating new task status with data: {}", createDTO);
        var statusDTO = statusService.createStatus(createDTO);
        log.info("Task status created successfully: {}", statusDTO);
        return statusDTO;
    }

    @Operation(
            summary = "Обновить статус задачи",
            description = "Обновляет статус задачи по указанному ID, "
                    + "операция доступна пользователям с ролью администратор")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskStatusDTO updateTaskStatus(@RequestBody @Valid TaskStatusUpdateDTO updateDTO, @PathVariable Long id) {
        log.info("Request to update task status with id: {}, to  update data: {}", id, updateDTO);
        var statusDTO = statusService.updateStatus(updateDTO, id);
        log.info("Task status updated successfully: {}", statusDTO);
        return statusDTO;
    }

    @Operation(
            summary = "Удалить статус задачи",
            description = "Удаляет статус задачи по указанному ID, "
                    + "операция доступна пользователям с ролью администратор")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTaskStatus(@PathVariable Long id) {
        log.info("Request to delete task status with id: {}", id);
        statusService.deleteStatus(id);
        log.info("Task status delete successfully with id: {}", id);
    }
}
