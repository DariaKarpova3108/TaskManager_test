package com.example.app.repositories;

import com.example.app.models.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskPriorityRepository extends JpaRepository<TaskPriority, Long> {
    Optional<TaskPriority> findByPriorityName(String priorityName);
    boolean existsByPriorityName(String priorityName);
}
