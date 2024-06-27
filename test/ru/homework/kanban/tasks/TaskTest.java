package ru.homework.kanban.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.homework.kanban.manager.Managers;
import ru.homework.kanban.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Задача")
class TaskTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    @DisplayName("Задача должна совпадать с копией")
    void shouldBeEqualsToCopy() {
        Task task = new Task("название", "описание", TaskStatus.NEW);
        int taskId = taskManager.addNewTask(task);
        Task taskToComparison = new Task(taskId, "название", "описание", TaskStatus.NEW);
        assertEquals(task, taskToComparison, "Задача и её копия не совпадают");
    }
}