package com.example.crudapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.crudapi.dto.CreateTaskRequest;
import com.example.crudapi.dto.PatchTaskRequest;
import com.example.crudapi.dto.TaskResponse;
import com.example.crudapi.dto.UpdateTaskRequest;
import com.example.crudapi.mapper.TaskMapper;
import com.example.crudapi.model.Task;
import com.example.crudapi.repository.TaskRepository;

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
      return taskRepository.findByTitleContainingIgnoreCaseAndCompletedAndIsActive(title, completed, true, pageable)
          .map(TaskMapper::toResponse);
    } else if (title != null) {
      log.info("Fetching tasks by title '{}' with pageable: {}", title, pageable);
      return taskRepository.findByTitleContainingIgnoreCaseAndIsActive(title, true, pageable)
          .map(TaskMapper::toResponse);
    } else if (completed != null) {
      log.info("Fetching tasks by completed status '{}' with pageable: {}", completed, pageable);
      return taskRepository.findByCompletedAndIsActive(completed, true, pageable)
          .map(TaskMapper::toResponse);
    } else {
      log.info("Fetching all active tasks with pageable: {}", pageable);
      return taskRepository.findAllByIsActive(true, pageable)
          .map(TaskMapper::toResponse);
    }
  }

  public TaskResponse getTaskById(Integer id) {
    log.info("Fetching active task by ID: {}", id);
    Task task = findActiveTaskById(id);
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
    Task existingTask = findActiveTaskById(id);
    existingTask.setTitle(request.getTitle());
    existingTask.setCompleted(request.isCompleted());
    Task updatedTask = taskRepository.save(existingTask);
    log.info("Task with ID: {} updated successfully.", updatedTask.getId());
    return TaskMapper.toResponse(updatedTask);
  }

  public TaskResponse patchTask(Integer id, PatchTaskRequest request) {
    log.info("Patching task with ID: {}", id);
    Task existingTask = findActiveTaskById(id);

    if (request.getTitle() != null) {
      existingTask.setTitle(request.getTitle());
    }

    if (request.getCompleted() != null) {
      existingTask.setCompleted(request.getCompleted());
    }

    Task patchedTask = taskRepository.save(existingTask);
    log.info("Task with ID: {} patched successfully.", patchedTask.getId());
    return TaskMapper.toResponse(patchedTask);
  }

  public void deleteTask(Integer id) {
    log.info("Soft-deleting task with ID: {}", id);
    Task existingTask = taskRepository.findById(id)
        .orElseThrow(() -> {
          log.error("Task not found with ID: {} for deletion.", id);
          return new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id);
        });
    existingTask.setActive(false);
    taskRepository.save(existingTask);
    log.info("Task with ID: {} soft-deleted successfully.", id);
  }

  private Task findActiveTaskById(Integer id) {
    log.debug("Attempting to find active task entity by ID: {}", id);
    return taskRepository.findByIdAndIsActive(id, true)
        .orElseThrow(() -> {
          log.error("Active task not found with ID: {}", id);
          return new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id);
        });
  }
}
