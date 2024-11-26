package com.example.app.controllers.api;

import com.example.app.dto.specificationDTO.TaskParamDTO;
import com.example.app.dto.task.TaskCreateDTO;
import com.example.app.dto.task.TaskDTO;
import com.example.app.dto.task.TaskUpdateDTO;
import com.example.app.dto.task.TaskUpdateForAssigneeDTO;
import com.example.app.services.TaskService;
import com.example.app.utils.TaskUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Контроллер задач", description = "Позволяет проводить CRUD операции с задачами")
public class TaskController {
    private final TaskService taskService;
    private final TaskUtils taskUtils;

    @Operation(summary = "Получить список задач",
            description = "Возвращает список задач в соответствии с заданными параметрами фильтрации, "
                    + "пагинацией и сортировкой")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskDTO>> getListTask(TaskParamDTO paramDTO,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "id,asc") String sort) {
        log.info("Fetching all tasks corresponding to the request parameters: {} and sorted: {}, number of pages: {}",
                paramDTO, sort, page);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskService.totalCountListOfTask(paramDTO)))
                .body(taskService.getAllTask(paramDTO, page, sort));
    }

    @Operation(summary = "Получить задачу по ID",
            description = "Возвращает данные задачи по указанному идентификатору")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public TaskDTO getTask(@PathVariable Long id) {
        log.info("Fetching task with ID: {}", id);
        return taskService.getTask(id);
    }

    @Operation(summary = "Создать задачу",
            description = "Создает новую задачу на основе предоставленных данных")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskDTO createTask(@RequestBody @Valid TaskCreateDTO createDTO) {
        log.info("Request to create task: {}", createDTO);
        var taskDTO = taskService.createTask(createDTO);
        log.info("Task created successfully: {}", taskDTO);
        return taskDTO;
    }

    @Operation(summary = "Обновить задачу",
            description = "Позволяет обновлять данные задачи по указанному идентификатору с ролью администратор")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskDTO updateTask(@RequestBody @Valid TaskUpdateDTO updateDTO,
                              @PathVariable Long id) {
        log.info("Request to update task with role ADMIN with id: {}, to  update data: {}", id, updateDTO);
        var taskDTO = taskService.updateTask(updateDTO, id);
        log.info("Task updated successfully with role ADMIN: {}", taskDTO);
        return taskDTO;
    }

    @Operation(summary = "Обновить задачу исполнителем",
            description = "Позволяет обновлять данные задачи по указанному идентификатору "
                    + "исполнителем или администратором.")
    @PutMapping("/{id}/assignee-update")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @taskUtils.isAssigneeOrAdmin(#id, principal)")
    public TaskDTO updateTaskForAssignee(@RequestBody @Valid TaskUpdateForAssigneeDTO updateDTO,
                                         @PathVariable Long id) {
        log.info("Request to update task with role USER and assignee for this task with id: {},"
                + " to  update data: {}", id, updateDTO);
        var taskDTO = taskService.updateTaskForAssignee(updateDTO, id);
        log.info("Task updated successfully with role USER and assignee for this task: {}", taskDTO);
        return taskDTO;
    }


    @Operation(summary = "Удалить задачу",
            description = "Удаляет задачу по указанному идентификатору")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTask(@PathVariable Long id) {
        log.info("Request to delete task with id: {}", id);
        taskService.deleteTask(id);
        log.info("Task delete successfully with id: {}", id);
    }
}
