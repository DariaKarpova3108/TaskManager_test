package com.example.app.repositories;

import com.example.app.models.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    List<TaskComment> findByTaskId(Long taskId);
    Optional<TaskComment> findByIdAndTaskId(Long id, Long taskId);
}
