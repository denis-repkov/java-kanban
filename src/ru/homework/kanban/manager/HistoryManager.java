package ru.homework.kanban.manager;

import ru.homework.kanban.tasks.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistoryTask();

    void addTask(Task task);

    void removeTask(int id);
}
