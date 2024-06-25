package ru.homework.kanban.manager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        new FileBackedTaskManager(getDefaultHistory());
        return FileBackedTaskManager.loadFromFile(new File("resources/backupTasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
