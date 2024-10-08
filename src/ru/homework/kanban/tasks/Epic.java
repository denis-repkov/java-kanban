package ru.homework.kanban.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    protected List<Integer> subtaskIds = new ArrayList<>();
    protected LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
    }

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", type=" + getType() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", subtaskIds=" + subtaskIds +
                ", duration=" + getDuration() + '\'' +
                ", startTime=" + getStartTime() + '\'' +
                ", endTime=" + endTime + '\'' +
                '}';
    }
}
