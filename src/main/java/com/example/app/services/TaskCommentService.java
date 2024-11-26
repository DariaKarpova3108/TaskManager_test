package com.example.app.services;

import com.example.app.dto.taskComment.TaskCommentCreateDTO;
import com.example.app.dto.taskComment.TaskCommentDTO;
import com.example.app.dto.taskComment.TaskCommentUpdateDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.mappers.TaskCommentMapper;
import com.example.app.repositories.TaskCommentRepository;
import com.example.app.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskCommentService {
    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final TaskCommentMapper commentMapper;

    public List<TaskCommentDTO> getAllCommentsForTask(Long taskId) {
        log.info("Fetching all comments for task with id: {}", taskId);
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task with id: {} not found.", taskId);
                    return new ResourceNotFoundException("Task with id: " + taskId + " not found");
                });

        var comments = commentRepository.findByTaskId(task.getId()).stream()
                .map(commentMapper::map)
                .collect(Collectors.toList());

        log.info("Successfully fetched {} comments for task with id: {}", comments.size(), taskId);
        return comments;
    }

    public TaskCommentDTO getCommentForTask(Long taskId, Long id) {
        log.info("Fetching comment with id: {} for task with id: {}", id, taskId);

        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task with id: {} not found.", taskId);
                    return new ResourceNotFoundException("Task with id: " + taskId + " not found");
                });

        var taskComment = commentRepository.findByIdAndTaskId(id, taskId)
                .orElseThrow(() -> {
                    log.error("Comment with id: {} not found for task with id: {}", id, taskId);
                    return new ResourceNotFoundException("Task comment with id: " + id
                            + " not found for task with id:" + taskId);
                });

        log.info("Successfully fetched comment with id: {} for task with id: {}", id, taskId);
        return commentMapper.map(taskComment);
    }

    public TaskCommentDTO createCommentForTask(Long taskId, TaskCommentCreateDTO createDTO) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task with id: {} not found.", taskId);
                    return new ResourceNotFoundException("Task with id: " + taskId + " not found");
                });

        var taskComment = commentMapper.map(createDTO);
        taskComment.setTask(task);
        commentRepository.save(taskComment);
        return commentMapper.map(taskComment);
    }

    public TaskCommentDTO updateCommentForTask(Long taskId, TaskCommentUpdateDTO updateDTO, Long id) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task with id: {} not found.", taskId);
                    return new ResourceNotFoundException("Task with id: " + taskId + " not found");
                });

        var taskComment = commentRepository.findByIdAndTaskId(id, taskId)
                .orElseThrow(() -> {
                    log.error("Comment with id: {} not found for task with id: {}", id, taskId);
                    return new ResourceNotFoundException("Task comment with id: " + id
                            + " not found for task with id:" + taskId);
                });

        commentMapper.update(updateDTO, taskComment);
        commentRepository.save(taskComment);
        return commentMapper.map(taskComment);
    }

    public void deleteCommentForTask(Long taskId, Long id) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task with id: {} not found.", taskId);
                    return new ResourceNotFoundException("Task with id: " + taskId + " not found");
                });

        var taskComment = commentRepository.findByIdAndTaskId(id, taskId)
                .orElseThrow(() -> {
                    log.error("Comment with id: {} not found for task with id: {}", id, taskId);
                    return new ResourceNotFoundException("Task comment with id: " + id
                            + " not found for task with id:" + taskId);
                });

        commentRepository.delete(taskComment);
    }
}
