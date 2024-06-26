package ru.homework.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.homework.kanban.tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("В памяти истории менеджера задач")
class InMemoryHistoryManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    @DisplayName("В истории 3 объекта")
    public void shouldBe3Objects(){
        int taskId1 = taskManager.addNewTask(new Task("Task1", "1", TaskStatus.NEW));
        taskManager.getTask(taskId1);
        int epicId1 = taskManager.addNewEpic(new Epic("Epic1", "1"));
        Epic epic1 = (Epic) taskManager.getEpic(epicId1);
        int subTaskId2 = taskManager.addNewSubtask(new Subtask("Subtask1", "1", TaskStatus.NEW, epic1.getId()));
        taskManager.getSubtask(subTaskId2);

        List<Task> history = taskManager.getHistory();

        int lengthShouldBe = 3;

        assertEquals(lengthShouldBe, history.size(), "Не все просмотры добавлены");
    }

    @Test
    @DisplayName("сохряняется 1 копия задачи")
    public void shouldBe1CopyOfTask() {
        int taskId1 = taskManager.addNewTask(new Task("Task1", "1", TaskStatus.NEW));
        taskManager.getTask(taskId1);
        taskManager.getTask(taskId1);
        taskManager.getTask(taskId1);

        List<Task> history = taskManager.getHistory();

        int lengthShouldBe = 1;

        assertEquals(lengthShouldBe, history.size(), "В истории несколько просмотров таски");
    }

    @Test
    @DisplayName("Задача исчезает из истории после удаления")
    public void disappearFromTheHistoryAfterDeletion() {
        int taskId1 = taskManager.addNewTask(new Task(" ", " ", TaskStatus.NEW));
        taskManager.getTask(taskId1);
        taskManager.deleteTask(taskId1);

        List<Task> history = taskManager.getHistory();

        int lengthShouldBee = 0;

        assertEquals(lengthShouldBee, history.size(), "В истории сохранен просмотр после удаления");
    }
}