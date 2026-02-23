package com.example.demo.controller;

import java.util.List;

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

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.dto.UpdateTaskRequest;
import com.example.demo.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TaskResponse createTask(@RequestBody @Valid CreateTaskRequest request) {
    return taskService.createTask(request);
  }

  @GetMapping
  public List<TaskResponse> getAllTasks() {
    return taskService.getAllTasks();
  }

  @GetMapping("/{id}")
  public TaskResponse getTaskById(@PathVariable Integer id) {
    return taskService.getTaskById(id);
  }

  @PutMapping("/{id}")
  public TaskResponse updateTask(@PathVariable Integer id, @RequestBody @Valid UpdateTaskRequest request) {
    return taskService.updateTask(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTask(@PathVariable Integer id) {
    taskService.deleteTask(id);
  }

  @GetMapping("/completed")
  public List<TaskResponse> getTasksByCompletedStatus(@RequestParam boolean completed) {
    return taskService.findByCompleted(completed);
  }

  @GetMapping("/title")
  public List<TaskResponse> getTasksByTitle(@RequestParam String title) {
    return taskService.findByTitle(title);
  }

}
