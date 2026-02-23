package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.dto.UpdateTaskRequest;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;

@Service
public class TaskService {

  private static final Logger log = LoggerFactory.getLogger(TaskService.class);

  private final TaskRepository taskRepository;

  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public Page<TaskResponse> getAllTasks(String title, Boolean completed, Pageable pageable) {
    if (title != null && completed != null) {
      log.info("Fetching tasks by title '{}' and completed status '{}' with pageable: {}", title, completed, pageable);
      return taskRepository.findByTitleContainingIgnoreCaseAndCompleted(title, completed, pageable)
          .map(TaskMapper::toResponse);
    } else if (title != null) {
      log.info("Fetching tasks by title '{}' with pageable: {}", title, pageable);
      return taskRepository.findByTitleContainingIgnoreCase(title, pageable)
          .map(TaskMapper::toResponse);
    } else if (completed != null) {
      log.info("Fetching tasks by completed status '{}' with pageable: {}", completed, pageable);
      return taskRepository.findByCompleted(completed, pageable)
          .map(TaskMapper::toResponse);
    } else {
      log.info("Fetching all tasks with pageable: {}", pageable);
      return taskRepository.findAll(pageable)
          .map(TaskMapper::toResponse);
    }
  }

  public TaskResponse getTaskById(Integer id) {
    log.info("Fetching task by ID: {}", id);
    Task task = findTaskById(id);
    return TaskMapper.toResponse(task);
  }

  public TaskResponse createTask(CreateTaskRequest request) {
    log.info("Creating new task with title: {}", request.getTitle());
    Task newTask = TaskMapper.toEntity(request);
    Task savedTask = taskRepository.save(newTask);
    log.info("Task created with ID: {}", savedTask.getId());
    return TaskMapper.toResponse(savedTask);
  }

  public TaskResponse updateTask(Integer id, UpdateTaskRequest request) {
    log.info("Updating task with ID: {}", id);
    Task existingTask = findTaskById(id);
    existingTask.setTitle(request.getTitle());
    existingTask.setCompleted(request.isCompleted());
    Task updatedTask = taskRepository.save(existingTask);
    log.info("Task with ID: {} updated successfully.", updatedTask.getId());
    return TaskMapper.toResponse(updatedTask);
  }

  public void deleteTask(Integer id) {
    log.info("Deleting task with ID: {}", id);
    Task existingTask = findTaskById(id);
    taskRepository.delete(existingTask);
    log.info("Task with ID: {} deleted successfully.", id);
  }

  private Task findTaskById(Integer id) {
    log.debug("Attempting to find task entity by ID: {}", id);
    return taskRepository.findById(id)
        .orElseThrow(() -> {
          log.error("Task not found with ID: {}", id);
          return new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id);
        });
  }
}
