package com.example.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;

@Service
public class TaskService {

  private final TaskRepository taskRepository;

  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public List<Task> getAllTasks() {
    return taskRepository.findAll();
  }

  public Task getTaskById(Integer id) {
    return taskRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
  }

  public Task createTask(Task newTask) {
    return taskRepository.save(newTask);
  }

  public Task updateTask(Integer id, Task taskDetails) {
    Task existingTask = getTaskById(id);
    existingTask.setTitle(taskDetails.getTitle());
    existingTask.setCompleted(taskDetails.isCompleted());
    return taskRepository.save(existingTask);
  }

  public void deleteTask(Integer id) {
    Task existingTask = getTaskById(id);
    taskRepository.delete(existingTask);
  }

  public List<Task> findByCompleted(boolean completed) {
    return taskRepository.findByCompleted(completed);
  }

  public List<Task> findByTitle(String title) {
    return taskRepository.findByTitleContainingIgnoreCase(title);
  }
}
