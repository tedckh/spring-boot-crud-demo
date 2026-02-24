package com.example.crudapi.mapper;

import com.example.crudapi.dto.CreateTaskRequest;
import com.example.crudapi.dto.TaskResponse;
import com.example.crudapi.model.Task;

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
    return new TaskResponse(task.getId(), task.getTitle(), task.isCompleted(),
        task.isActive(), task.getCreatedBy(), task.getCreatedDate(),
        task.getLastModifiedBy(), task.getLastModifiedDate());
  }
}
