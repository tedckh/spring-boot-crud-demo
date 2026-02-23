package com.example.demo.dto;

public class TaskResponse {
  private Integer id;
  private String title;
  private boolean completed;

  public TaskResponse(Integer id, String title, boolean completed) {
    this.id = id;
    this.title = title;
    this.completed = completed;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

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
