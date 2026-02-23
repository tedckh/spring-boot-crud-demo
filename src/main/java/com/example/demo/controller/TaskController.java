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
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Task;
import com.example.demo.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Task createTask(@RequestBody Task newTask) {
    return taskService.createTask(newTask);
  }

  @GetMapping
  public List<Task> getAllTasks() {
    return taskService.getAllTasks();
  }

  @GetMapping("/{id}")
  public Task getTaskById(@PathVariable Integer id) {
    return taskService.getTaskById(id);
  }

  @PutMapping("/{id}")
  public Task updateTask(@PathVariable Integer id, @RequestBody Task updatedTask) {
    return taskService.updateTask(id, updatedTask);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTask(@PathVariable Integer id) {
    taskService.deleteTask(id);
  }

  @GetMapping("/completed")
  public List<Task> getTasksByCompletedStatus(@RequestParam boolean completed) {
    return taskService.findByCompleted(completed);
  }

  @GetMapping("/title")
  public List<Task> getTasksByTitle(@RequestParam String title) {
    return taskService.findByTitle(title);
  }

}
