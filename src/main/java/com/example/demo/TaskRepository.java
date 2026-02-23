package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
  List<Task> findByTitle(String title);

  List<Task> findByTitleContainingIgnoreCase(String keyword);

  List<Task> findByCompleted(boolean completed);
}
