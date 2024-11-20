package com.example.app.controllers.api;

import com.example.app.dto.taskComment.TaskCommentCreateDTO;
import com.example.app.dto.taskComment.TaskCommentDTO;
import com.example.app.dto.taskComment.TaskCommentUpdateDTO;
import com.example.app.services.TaskCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class TaskCommentController {
    private final TaskCommentService commentService;

    @GetMapping
    public ResponseEntity<List<TaskCommentDTO>> getListTaskComments() {
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(commentService.getAllComments().size()))
                .body(commentService.getAllComments());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskCommentDTO getTaskComments(@PathVariable Long id) {
        return commentService.getComment(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskCommentDTO createTaskComments(@RequestBody @Valid TaskCommentCreateDTO createDTO) {
        return commentService.createComment(createDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskCommentDTO updateTaskComments(@RequestBody @Valid TaskCommentUpdateDTO updateDTO,
                                          @PathVariable Long id) {
        return commentService.updateComment(updateDTO, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskComments(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
