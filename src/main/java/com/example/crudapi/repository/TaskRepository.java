package com.example.crudapi.repository;

import com.example.crudapi.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

  Page<Task> findAllByIsActive(boolean isActive, Pageable pageable);

  Page<Task> findByTitleContainingIgnoreCaseAndIsActive(String keyword, boolean isActive, Pageable pageable);

  Page<Task> findByCompletedAndIsActive(boolean completed, boolean isActive, Pageable pageable);

  Page<Task> findByTitleContainingIgnoreCaseAndCompletedAndIsActive(String keyword, boolean completed, boolean isActive,
      Pageable pageable);

  Optional<Task> findByIdAndIsActive(Integer id, boolean isActive);
}
