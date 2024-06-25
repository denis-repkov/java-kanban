package ru.homework.kanban.tasks;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String toFileString() {
        String[] string = {Integer.toString(getId()),
                getType().toString(),
                getName(),
                getStatus().toString(),
                getDescription(),
                null};
        return String.join(",", string);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
               Objects.equals(name, task.name) &&
               Objects.equals(description, task.description) &&
               Objects.equals(status, task.status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", type=" + TaskType.TASK +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
