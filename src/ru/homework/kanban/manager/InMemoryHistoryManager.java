package ru.homework.kanban.manager;

import ru.homework.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyTask = new ArrayList<>();

    @Override
    public List<Task> getHistoryTask() {
        return historyTask;
    }

    @Override
    public void addTask(Task task) {
        if (historyTask.isEmpty() || historyTask.size() < 10) {
            historyTask.add(task);
        } else {
            historyTask.remove(0);
            historyTask.add(task);
        }
    }
}
