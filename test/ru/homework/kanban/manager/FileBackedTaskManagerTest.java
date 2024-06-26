package ru.homework.kanban.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.homework.kanban.tasks.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Восстановление менеджера задач из файла")
class FileBackedTaskManagerTest {
    TaskManager manager = Managers.getDefault();
    TaskManager loadedManager;
    @AfterEach


    @Test
    @DisplayName("Сохраненные и загруженные задачи/эпики/подзадачи одинаковые")
    public void saveAndLoadedTaskAreEqual() {
        int taskId = manager.addNewTask(new Task(" ", " ", TaskStatus.NEW));
        manager.getTask(taskId);
        int epicId = manager.addNewEpic(new Epic(" ", " "));
        manager.getEpic(epicId);
        int subtaskId = manager.addNewSubtask(new Subtask("", "", TaskStatus.NEW, 2));
        manager.getSubtask(subtaskId);
        loadedManager = FileBackedTaskManager.loadFromFile(new File("resources/backupTasks.csv"));
        assertEquals(manager.getTask(taskId).toString(), loadedManager.getTask(taskId).toString(), "Задачи не одинаковы");
        assertEquals(manager.getEpic(epicId).toString(), loadedManager.getEpic(epicId).toString(), "Эпики не одинаковы");
        assertEquals(manager.getSubtask(subtaskId).toString(), loadedManager.getSubtask(subtaskId).toString(), "Подзадачи не одинаковы");
    }

    @Test
    @DisplayName("Сохранение и загрузка пустого файла")
    public void saveAbdLoadEmptyFile() throws IOException {
        loadedManager = FileBackedTaskManager.loadFromFile(File.createTempFile("resources/", "testBackupTasks.csv"));
        List<Task> list = loadedManager.getTasks();
        int lengthShouldBe = 0;
        assertEquals(lengthShouldBe, list.size(), "Загружен не пустой файл");
        loadedManager.addNewTask(new Task("", "", TaskStatus.NEW));

        List<Task> list1 = loadedManager.getTasks();
        int lengthShouldBe1 = 1;
        assertEquals(lengthShouldBe1, list1.size(), "Файл не записан");
    }
}