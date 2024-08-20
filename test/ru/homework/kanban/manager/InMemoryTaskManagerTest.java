package ru.homework.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.homework.kanban.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("В памяти менеджера задач")
class InMemoryTaskManagerTest {

    private TaskManager taskManager = Managers.getDefault();

    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    public void createManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    @DisplayName("Добавление в список и поиск по ID")
    public void shouldAddAngGetById(){
        task = new Task("Test task", "Test createTask description", TaskStatus.NEW, Duration.ofMinutes(0), LocalDateTime.now());
        int taskId = taskManager.addNewTask(task);
        epic = new Epic("Test epic", "Test createEpic description");
        int epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Test subtask", "Test createSubtask description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(0), LocalDateTime.now());
        int subTaskId = taskManager.addNewSubtask(subtask);

        assertEquals(task, taskManager.getTask(taskId), "Сохраненные задачи не совпадают");
        assertEquals(epic, taskManager.getEpic(epicId), "Сохраненные эпики не совпадают");
        assertEquals(subtask, taskManager.getSubtask(subTaskId), "Сохраненные подзадачи не совпадают");
    }

    @Test
    @DisplayName("Сравнение созданной и сохраненной задач по каждому параметру")
    public void equalsTask() {
        task = new Task("Test task", "Test createTask description", TaskStatus.NEW, Duration.ofMinutes(0), LocalDateTime.now());
        int taskId = taskManager.addNewTask(task);

        Task savedTask = taskManager.getTask(taskId);

        assertEquals(task.getDescription(), savedTask.getDescription(),  "Не совпадает по описанию");
        assertEquals(task.getId(), savedTask.getId(),  "Не совпадает по ID");
        assertEquals(task.getName(), savedTask.getName(),  "Не совпадает по названию");
        assertEquals(task.getStatus(), savedTask.getStatus(),  "Не совпадает по статусу");
        assertEquals(task.getDuration(), savedTask.getDuration(), "Не совпадает по продолжительности");
        assertEquals(task.getStartTime(), savedTask.getStartTime(), "Не совпадает по дате и времени начала");
    }
    @Test
    @DisplayName("Сравнение созданных и сохраненных эпиков и подзадач по каждому параметру")
    public void equalsEpic() {
        epic = new Epic("Test epic", "Test createEpic description");
        int epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Test subtask", "Test createSubtask description", TaskStatus.NEW, epicId, Duration.ofMinutes(0), LocalDateTime.now());
        taskManager.addNewSubtask(subtask);

        Epic savedEpic = (Epic) taskManager.getEpic(epicId);
        Subtask savedSubtask = taskManager.getSubtask(subtask.getId());

        assertEquals(epic.getDescription(), savedEpic.getDescription(),  "Не совпадает по описанию");
        assertEquals(epic.getId(), savedEpic.getId(),  "Не совпадает по ID");
        assertEquals(epic.getName(), savedEpic.getName(),  "Не совпадает по названию");
        assertEquals(epic.getStatus(), savedEpic.getStatus(),  "Не совпадает по статусу");
        assertEquals(epic.getSubtaskIds(), savedEpic.getSubtaskIds(),  "Не совпадает по подзадачам");
        assertEquals(epic.getDuration(), savedEpic.getDuration(), "Не совпадает по продолжительности");
        assertEquals(epic.getStartTime(), savedEpic.getStartTime(), "Не совпадает по дате и времени начала");

        assertEquals(subtask.getDescription(), savedSubtask.getDescription(),  "Не совпадает по описанию");
        assertEquals(subtask.getId(), savedSubtask.getId(),  "Не совпадает по ID");
        assertEquals(subtask.getName(), savedSubtask.getName(),  "Не совпадает по названию");
        assertEquals(subtask.getStatus(), savedSubtask.getStatus(),  "Не совпадает по статусу");
        assertEquals(subtask.getEpicId(), savedSubtask.getEpicId(),  "Не совпадает по эпику");
        assertEquals(subtask.getDuration(), savedSubtask.getDuration(), "Не совпадает по продолжительности");
        assertEquals(subtask.getStartTime(), savedSubtask.getStartTime(), "Не совпадает по дате и времени начала");
    }
}