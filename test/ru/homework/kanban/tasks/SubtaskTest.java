package ru.homework.kanban.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.homework.kanban.manager.Managers;
import ru.homework.kanban.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Поздазача")
class SubtaskTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    @DisplayName("Подзадача должна совпадать с копией")
    void shouldBeEqualsToCopy() {
        Epic epic = new Epic("название", "описание");
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("название", "описание", TaskStatus.NEW, epicId);
        int subtaskId = taskManager.addNewSubtask(subtask);
        Task taskToComparison = new Subtask(subtaskId, "название", "описание", TaskStatus.NEW, epicId);

        assertEquals(subtask, taskToComparison, "Подзадача и её копия не совпадают");
    }
}