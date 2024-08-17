package ru.homework.kanban;

import ru.homework.kanban.manager.Managers;
import ru.homework.kanban.manager.TaskManager;
import ru.homework.kanban.tasks.Epic;
import ru.homework.kanban.tasks.Subtask;
import ru.homework.kanban.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static ru.homework.kanban.tasks.TaskStatus.NEW;
import static ru.homework.kanban.tasks.TaskStatus.IN_PROGRESS;
import static ru.homework.kanban.tasks.TaskStatus.DONE;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        LocalDateTime now = LocalDateTime.now();

        System.out.println("Проверка начинается!");
        // добавляем задачи/эпики/подзадачи для теста
        taskManager.addNewTask(new Task("Задача-1", "Здесь описание задачи 1", NEW, Duration.ofMinutes(1), now));
        taskManager.addNewTask(new Task("Задача-2", "Здесь описание задачи 2", NEW, Duration.ofMinutes(1), now.plusMinutes(1)));
        taskManager.addNewEpic(new Epic("Эпик-1", "Здесь описание эпика 1"));
        taskManager.addNewSubtask(new Subtask("Подзадача-1-1", "Здесь описание подзадачи 1-1", NEW, 3, Duration.ofMinutes(1), now.plusMinutes(2)));
        taskManager.addNewSubtask(new Subtask("Подзадача-1-2", "Здесь описание подзадачи 1-2", NEW, 3, Duration.ofMinutes(1), now.plusMinutes(3)));
        taskManager.addNewSubtask(new Subtask("Подзадача-1-3", "Здесь описание подзадачи 1-3", NEW, 3, Duration.ofMinutes(1), now.plusMinutes(4)));
        taskManager.addNewEpic(new Epic("Эпик-2", "Здесь описание эпика 1"));
        taskManager.addNewSubtask(new Subtask("Подзадача-2-1", "Здесь описание подзадачи 2-1", NEW, 7, Duration.ofMinutes(1), now.plusMinutes(5)));
        taskManager.addNewSubtask(new Subtask("Подзадача-2-2", "Здесь описание подзадачи 2-2", NEW, 7, Duration.ofMinutes(1), now.plusMinutes(6)));
        taskManager.addNewSubtask(new Subtask("Подзадача-2-3", "Здесь описание подзадачи 2-3", DONE, 7, Duration.ofMinutes(1), now.plusMinutes(7)));

        System.out.println("Выводим в консоль списки всех задач/эпиков/подзадач...");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        System.out.println("\nВыводим в консоль 1 задачу/эпик/подзадачу...");
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getEpic(3));
        System.out.println(taskManager.getSubtask(8));

        System.out.println("\nПроверяем вывод истории просмотров...");
        System.out.println(taskManager.getHistory());

        System.out.println("\nДелаем накрутку истории просмотров и выводим историю снова...");
        taskManager.getSubtask(4);
        taskManager.getSubtask(5);
        taskManager.getSubtask(6);
        taskManager.getTask(2);
        taskManager.getSubtask(8);
        taskManager.getSubtask(9);
        taskManager.getSubtask(10);
        taskManager.getEpic(7);
        taskManager.getTask(2);
        System.out.println(taskManager.getHistory());


        System.out.println("\nВыводим в консоль все подзадачи эпика...");
        System.out.println(taskManager.getEpicSubtasks(7));

        System.out.println("\nОбновляем данные по задаче и печатаем список задач для проверки...");
        taskManager.updateTask(new Task(1, "Задача-1", "Здесь описание задачи 1 - обновлено", IN_PROGRESS, Duration.ofMinutes(1), now.plusHours(1)));
        System.out.println(taskManager.getTasks());

        System.out.println("\nОбновляем данные по подзадачам и печатаем список подзадач для проверки...");
        taskManager.updateSubtask(new Subtask(4, "Подзадача-1-1", "Здесь описание подзадачи 1", DONE, 3, Duration.ofMinutes(1), now.plusHours(1).plusMinutes(1)));
        taskManager.updateSubtask(new Subtask(5, "Подзадача-2-4", "Была 1-2 - стала 2-4", IN_PROGRESS, 7, Duration.ofMinutes(1), now.plusHours(1).plusMinutes(2)));
        System.out.println(taskManager.getSubtasks());

        System.out.println("\nВыводим снова список эпиков для проверки...");
        System.out.println(taskManager.getEpics());

        System.out.println("\nОбновляем данные по эпику и печатаем список эпиков для проверки...");
        taskManager.updateEpic(new Epic(7, "Эпик-2", "Добавилась подзадача 2-4"));
        System.out.println(taskManager.getEpics());

        System.out.println("\nПроверяем вывод в порядке приоритета...");
        System.out.println(taskManager.getPrioritizedTasks());

        System.out.println("\nУдаляем задачу и печатаем список задач для проверки...");
        taskManager.deleteTask(2);
        System.out.println(taskManager.getTasks());

        System.out.println("\nПроверяем, что задача также удалилась из истории просмотров...");
        System.out.println(taskManager.getHistory());

        System.out.println("\nУдаляем подзадачу и печатаем список подзадач и эпиков для проверки...");
        taskManager.deleteSubtask(6);
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getEpics());

        System.out.println("\nПроверяем, что подзадача также удалилась из истории просмотров...");
        System.out.println(taskManager.getHistory());

        System.out.println("\nУдаляем эпик и печатаем список подзадач и эпиков для проверки...");
        taskManager.deleteEpic(7);
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getEpics());

        System.out.println("\nПроверяем, что эпик и его подзадачи также удалились из истории просмотров...");
        System.out.println(taskManager.getHistory());

        System.out.println("\nУдаляем все задачи и печатаем список задач для проверки...");
        taskManager.deleteAllTasks();
        System.out.println(taskManager.getTasks());

        System.out.println("\nПроверяем, что задачи из истории просмотров также удалились...");
        System.out.println(taskManager.getHistory());

        System.out.println("\nУдаляем все подзадачи и печатаем список подзадач и эпиков для проверки...");
        taskManager.deleteAllSubtasks();
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getEpics());

        System.out.println("\nПроверяем, что подзадачи из истории  просмотров также удалились...");
        System.out.println(taskManager.getHistory());

        System.out.println("\nУдаляем все эпики и печатаем список эпиков для проверки...");
        taskManager.deleteAllEpics();
        System.out.println(taskManager.getEpics());

        System.out.println("\nПроверяем, что эпики из истории просмотров также удалились...");
        System.out.println(taskManager.getHistory());

    }
}
