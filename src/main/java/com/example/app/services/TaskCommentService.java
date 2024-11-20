package com.example.app.services;

import com.example.app.dto.taskComment.TaskCommentCreateDTO;
import com.example.app.dto.taskComment.TaskCommentDTO;
import com.example.app.dto.taskComment.TaskCommentUpdateDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.mappers.TaskCommentMapper;
import com.example.app.repositories.TaskCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskCommentService {
    private final TaskCommentRepository commentRepository;
    private final TaskCommentMapper commentMapper;

    public List<TaskCommentDTO> getAllComments() {
        return commentRepository.findAll().stream()
                .map(commentMapper::map)
                .collect(Collectors.toList());
    }

    public TaskCommentDTO getComment(Long id) {
        var model = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task comment with id: " + id + " not found"));
        return commentMapper.map(model);
    }

    public TaskCommentDTO createComment(TaskCommentCreateDTO createDTO) {
        var model = commentMapper.map(createDTO);
        commentRepository.save(model);
        return commentMapper.map(model);
    }

    public TaskCommentDTO updateComment(TaskCommentUpdateDTO updateDTO, Long id) {
        var model = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task comment with id: " + id + " not found"));
        commentMapper.update(updateDTO, model);
        commentRepository.save(model);
        return commentMapper.map(model);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
