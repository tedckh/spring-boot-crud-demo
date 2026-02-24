package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.example.demo.base.BaseEntity;

@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
  @SequenceGenerator(name = "task_seq", sequenceName = "task_id_seq", allocationSize = 1)
  private Integer id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private boolean completed;

  public Task() {
  }

  public Task(String title, boolean completed) {
    this.title = title;
    this.completed = completed;
  }

  public Task(Integer id, String title, boolean completed) {
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

  @Override
  public String toString() {
    return "Task{" + "id=" + id + ", title='" + title + '\'' + ", completed=" + completed + '}';
  }
}
