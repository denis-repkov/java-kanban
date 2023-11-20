package ru.homework.kanban.manager;

import ru.homework.kanban.tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> historyTask = new LinkedList<>();

    @Override
    public List<Task> getHistoryTask() {
        return historyTask;
    }

    @Override
    public void addTask(Task task) {
        if (historyTask.size() < 10) {
            historyTask.add(task);
        } else {
            historyTask.remove(0);
            historyTask.add(task);
        }
    }
}
