package com.todoapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    public Task(int id, String description) {
        this.id = id;
        this.description = description;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return completed ? "Completa" : "Pendente";
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("[%d] %s - %s (Criada em: %s)",
            id, description, getStatus(), createdAt.format(formatter));
    }
}
