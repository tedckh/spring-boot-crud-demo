package com.example.demo.repository;

import com.example.demo.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
  Page<Task> findByTitle(String title, Pageable pageable);

  Page<Task> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

  Page<Task> findByCompleted(boolean completed, Pageable pageable);

  Page<Task> findByTitleContainingIgnoreCaseAndCompleted(String keyword, boolean completed, Pageable pageable);
}
