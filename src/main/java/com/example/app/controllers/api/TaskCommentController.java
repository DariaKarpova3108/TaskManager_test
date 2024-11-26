package com.example.app.controllers.api;

import com.example.app.dto.taskComment.TaskCommentCreateDTO;
import com.example.app.dto.taskComment.TaskCommentDTO;
import com.example.app.dto.taskComment.TaskCommentUpdateDTO;
import com.example.app.services.TaskCommentService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
@Tag(name = "Контроллер комментариев к задачам", description = "Позволяет проводить CRUD операции с комментариями, "
        + "связанными с задачами")
public class TaskCommentController {
    private final TaskCommentService commentService;
    private final TaskUtils taskUtils;

    @Operation(
            summary = "Получить список комментариев к задаче",
            description = "Возвращает список всех комментариев, связанных с указанной задачей по её идентификатору"
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TaskCommentDTO>> getListTaskComments(@PathVariable Long taskId) {
        log.info("Fetching all task comments with task id: {}", taskId);
        return ResponseEntity.ok()
                .header("X-Total-Count",
                        String.valueOf(commentService.getAllCommentsForTask(taskId).size()))
                .body(commentService.getAllCommentsForTask(taskId));
    }

    @Operation(
            summary = "Получить комментарий по его идентификатору",
            description = "Возвращает конкретный комментарий, связанный с указанной задачей и "
                    + "идентификатором комментария"
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public TaskCommentDTO getTaskComment(@PathVariable Long taskId, @PathVariable Long id) {
        log.info("Fetching task comment with id {}, where taskId: {}", id, taskId);
        return commentService.getCommentForTask(taskId, id);
    }

    @Operation(
            summary = "Создать новый комментарий для задачи",
            description = "Создает новый комментарий для указанной задачи. "
                    + "Доступ ограничен только для назначенного пользователя-исполнителем или администратора"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or @taskUtils.isAssigneeOrAdmin(#id, principal)")
    public TaskCommentDTO createTaskComments(@PathVariable Long taskId,
                                             @RequestBody @Valid TaskCommentCreateDTO createDTO) {
        log.info("Request to create task comment: {}, where taskId: {}", createDTO, taskId);
        var commentDTO = commentService.createCommentForTask(taskId, createDTO);
        log.info("Task comment created successfully: {}", commentDTO);
        return commentDTO;
    }

    @Operation(
            summary = "Обновить комментарий",
            description = "Обновляет комментарий, связанный с указанной задачей. "
                    + "Только автор комментария или администратор могут выполнять эту операцию"
    )
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @taskUtils.isCommentAuthor(#id, principal)")
    public TaskCommentDTO updateTaskComments(@PathVariable Long taskId,
                                             @RequestBody @Valid TaskCommentUpdateDTO updateDTO,
                                             @PathVariable Long id) {
        log.info("Request to update task comment: {}, where taskId: {}", updateDTO, taskId);
        var commentDTO = commentService.updateCommentForTask(taskId, updateDTO, id);
        log.info("Task comment updated successfully: {}", commentDTO);
        return commentDTO;
    }

    @Operation(
            summary = "Удалить комментарий",
            description = "Удаляет указанный комментарий, связанный с задачей. "
                    + "Только автор комментария или администратор могут выполнить эту операцию"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @taskUtils.isCommentAuthor(#id, principal)")
    public void deleteTaskComments(@PathVariable Long taskId, @PathVariable Long id) {
        log.info("Request to delete task comment with id: {}, where taskId: {}", id, taskId);
        commentService.deleteCommentForTask(taskId, id);
        log.info("Task comment delete successfully with id: {}", id);
    }
}
