package com.example.crudapi.dto;

import jakarta.validation.constraints.Size;

public class PatchTaskRequest {

    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters if provided")
    private String title;

    private Boolean completed;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
