package com.example.crudapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.crudapi.dto.CreateTaskRequest;
import com.example.crudapi.dto.PatchTaskRequest;
import com.example.crudapi.dto.TaskResponse;
import com.example.crudapi.dto.UpdateTaskRequest;
import com.example.crudapi.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private static final Logger log = LoggerFactory.getLogger(TaskController.class);

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TaskResponse createTask(@RequestBody @Valid CreateTaskRequest request) {
    log.info("Received request to create task with title: {}", request.getTitle());
    return taskService.createTask(request);
  }

  @GetMapping
  public Page<TaskResponse> getAllTasks(
      @RequestParam(required = false) String title,
      @RequestParam(required = false) Boolean completed,
      Pageable pageable) {
    log.info("Received request to get tasks with filters - title: '{}', completed: '{}', pageable: {}",
        title, completed, pageable);
    return taskService.getAllTasks(title, completed, pageable);
  }

  @GetMapping("/{id}")
  public TaskResponse getTaskById(@PathVariable Integer id) {
    log.info("Received request to get task by ID: {}", id);
    return taskService.getTaskById(id);
  }

  @PutMapping("/{id}")
  public TaskResponse updateTask(@PathVariable Integer id, @RequestBody @Valid UpdateTaskRequest request) {
    log.info("Received request to update task with ID: {}", id);
    return taskService.updateTask(id, request);
  }

  @PatchMapping("/{id}")
  public TaskResponse patchTask(@PathVariable Integer id, @RequestBody @Valid PatchTaskRequest request) {
      log.info("Received request to patch task with ID: {}", id);
      return taskService.patchTask(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTask(@PathVariable Integer id) {
    log.info("Received request to delete task with ID: {}", id);
    taskService.deleteTask(id);
  }
}
