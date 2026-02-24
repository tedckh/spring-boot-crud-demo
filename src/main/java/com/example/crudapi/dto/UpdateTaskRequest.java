package com.example.crudapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateTaskRequest {

  @NotBlank(message = "Title cannot be blank")
  @Size(max = 255, message = "Title cannot exceed 255 characters")
  private String title;

  private boolean completed;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }
}
