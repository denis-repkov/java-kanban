package ru.homework.kanban.manager;

public class Managers {

    private Managers() {}
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
