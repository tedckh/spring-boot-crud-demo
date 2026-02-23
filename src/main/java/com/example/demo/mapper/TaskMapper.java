package com.example.demo.mapper;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.model.Task;

public class TaskMapper {

  public static Task toEntity(CreateTaskRequest request) {
    if (request == null) {
      return null;
    }
    return new Task(request.getTitle(), request.isCompleted());
  }

  public static TaskResponse toResponse(Task task) {
    if (task == null) {
      return null;
    }
    return new TaskResponse(task.getId(), task.getTitle(), task.isCompleted());
  }
}
