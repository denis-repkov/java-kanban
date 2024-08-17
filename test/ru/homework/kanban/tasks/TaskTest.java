package ru.homework.kanban.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.homework.kanban.manager.Managers;
import ru.homework.kanban.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Задача")
class TaskTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    @DisplayName("Задача должна совпадать с копией")
    void shouldBeEqualsToCopy() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task("название", "описание", TaskStatus.NEW, Duration.ofMinutes(0), now);
        int taskId = taskManager.addNewTask(task);
        Task taskToComparison = new Task(taskId, "название", "описание", TaskStatus.NEW, Duration.ofMinutes(0), now);
        assertEquals(task, taskToComparison, "Задача и её копия не совпадают");
    }
}