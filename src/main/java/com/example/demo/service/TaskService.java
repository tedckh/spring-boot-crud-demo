package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

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

  private final TaskRepository taskRepository;

  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public List<TaskResponse> getAllTasks() {
    return taskRepository.findAll().stream()
        .map(TaskMapper::toResponse)
        .collect(Collectors.toList());
  }

  public TaskResponse getTaskById(Integer id) {
    Task task = findTaskById(id);
    return TaskMapper.toResponse(task);
  }

  public TaskResponse createTask(CreateTaskRequest request) {
    Task newTask = TaskMapper.toEntity(request);
    Task savedTask = taskRepository.save(newTask);
    return TaskMapper.toResponse(savedTask);
  }

  public TaskResponse updateTask(Integer id, UpdateTaskRequest request) {
    Task existingTask = findTaskById(id);
    existingTask.setTitle(request.getTitle());
    existingTask.setCompleted(request.isCompleted());
    Task updatedTask = taskRepository.save(existingTask);
    return TaskMapper.toResponse(updatedTask);
  }

  public void deleteTask(Integer id) {
    Task existingTask = findTaskById(id);
    taskRepository.delete(existingTask);
  }

  public List<TaskResponse> findByCompleted(boolean completed) {
    return taskRepository.findByCompleted(completed).stream()
        .map(TaskMapper::toResponse)
        .collect(Collectors.toList());
  }

  public List<TaskResponse> findByTitle(String title) {
    return taskRepository.findByTitleContainingIgnoreCase(title).stream()
        .map(TaskMapper::toResponse)
        .collect(Collectors.toList());
  }

  private Task findTaskById(Integer id) {
    return taskRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id));
  }
}
