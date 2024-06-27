package ru.homework.kanban.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.homework.kanban.manager.Managers;
import ru.homework.kanban.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Эпик")
class EpicTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    @DisplayName("Эпик должен совпадать с копией")
    void shouldBeEqualsToCopy (){
        Epic epic = new Epic("название", "описание");
        int epicId = taskManager.addNewEpic(epic);
        Task taskToComparison = new Epic(epicId, "название", "описание");

        assertEquals(epic, taskToComparison, "Эпик и его копия не совпадают");
    }
}