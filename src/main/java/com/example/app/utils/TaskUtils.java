package com.example.app.utils;

import com.example.app.exception.ResourceNotFoundException;
import com.example.app.models.RoleName;
import com.example.app.repositories.TaskCommentRepository;
import com.example.app.repositories.TaskRepository;
import com.example.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class TaskUtils {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskCommentRepository commentRepository;

    public boolean isAssigneeOrAdmin(Long taskId, Principal principal) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + principal.getName()));

        return user.getRoles().stream().anyMatch(role -> role.getRoleName() == RoleName.ADMIN)
                || task.getAssignee().getId().equals(user.getId());
    }

    public boolean isCommentAuthor(Long commentId, Principal principal) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + principal.getName()));

        return comment.getAuthor().getId().equals(user.getId());
    }

}
