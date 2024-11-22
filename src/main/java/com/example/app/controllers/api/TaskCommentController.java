package com.example.app.controllers.api;

import com.example.app.dto.taskComment.TaskCommentCreateDTO;
import com.example.app.dto.taskComment.TaskCommentDTO;
import com.example.app.dto.taskComment.TaskCommentUpdateDTO;
import com.example.app.services.TaskCommentService;
import com.example.app.utils.TaskUtils;
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
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class TaskCommentController {
    private final TaskCommentService commentService;
    private final TaskUtils taskUtils;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TaskCommentDTO>> getListTaskComments(@PathVariable Long taskId) {
        return ResponseEntity.ok()
                .header("X-Total-Count",
                        String.valueOf(commentService.getAllCommentsForTask(taskId).size()))
                .body(commentService.getAllCommentsForTask(taskId));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public TaskCommentDTO getTaskComments(@PathVariable Long taskId, @PathVariable Long id) {
        return commentService.getCommentForTask(taskId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or @taskUtils.isAssigneeOrAdmin(#id, principal)")
    public TaskCommentDTO createTaskComments(@PathVariable Long taskId,
                                             @RequestBody @Valid TaskCommentCreateDTO createDTO) {
        return commentService.createCommentForTask(taskId, createDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @taskUtils.isAssigneeOrAdmin(#id, principal)")
    public TaskCommentDTO updateTaskComments(@PathVariable Long taskId,
                                             @RequestBody @Valid TaskCommentUpdateDTO updateDTO,
                                          @PathVariable Long id) {
        return commentService.updateCommentForTask(taskId, updateDTO, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @taskUtils.isAssigneeOrAdmin(#id, principal)")
    public void deleteTaskComments(@PathVariable Long taskId, @PathVariable Long id) {
        commentService.deleteCommentForTask(taskId, id);
    }
}
