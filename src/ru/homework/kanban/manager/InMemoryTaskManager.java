package ru.homework.kanban.manager;

import ru.homework.kanban.tasks.Task;
import ru.homework.kanban.tasks.Subtask;
import ru.homework.kanban.tasks.Epic;
import ru.homework.kanban.tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ru.homework.kanban.tasks.TaskStatus.NEW;
import static ru.homework.kanban.tasks.TaskStatus.IN_PROGRESS;
import static ru.homework.kanban.tasks.TaskStatus.DONE;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int counterId = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        if (epics.containsKey(epicId)) {
            ArrayList<Subtask> subtasksEpic = new ArrayList<>();
            ArrayList<Integer> subs = new ArrayList<>(epics.get(epicId).getSubtaskIds());
            for (Integer sub : subs) {
                subtasksEpic.add(subtasks.get(sub));
            }
            return subtasksEpic;
        }
        return null;
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.addTask(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = epics.get(id);
        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public int addNewTask(Task task) {
        final int id = ++counterId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            final int id = ++counterId;
            subtask.setId(id);
            subtasks.put(id, subtask);
            epics.get(subtask.getEpicId()).addSubtaskId(id);
            updateEpicStatus(subtask.getEpicId());
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = ++counterId;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача с ID " + task.getId() + " отсутствует!");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            int e = subtasks.get(subtask.getId()).getEpicId();
            epics.get(e).removeSubtaskId(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            updateEpicStatus(e);
            updateEpicStatus(subtask.getEpicId());
        } else {
            System.out.println("Подзадача с ID " + subtask.getId() + " отсутствует!");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            ArrayList<Integer> subtasksEpic = new ArrayList<>(epics.get(epic.getId()).getSubtaskIds());
            epics.put(epic.getId(), epic);
            for (int id : subtasksEpic) {
                epic.addSubtaskId(id);
            }
            updateEpicStatus(epic.getId());
        } else {
            System.out.println("Эпик с ID " + epic.getId() + " отсутствует!");
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.removeTask(id);
        } else {
            System.out.println("Задача с ID " + id + " отсутствует!");
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int e = subtasks.get(id).getEpicId();
            epics.get(e).removeSubtaskId(id);
            subtasks.remove(id);
            updateEpicStatus(e);
            historyManager.removeTask(id);
        } else {
            System.out.println("Подзадача с ID " + id + " отсутствует!");
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subtasksEpic = new ArrayList<>(epics.get(id).getSubtaskIds());
            for (int sub : subtasksEpic) {
                subtasks.remove(sub);
                historyManager.removeTask(sub);
            }
            epics.get(id).clearSubtaskIds();
            epics.remove(id);
            historyManager.removeTask(id);
        } else {
            System.out.println("Эпик с ID " + id + " отсутствует!");
        }
    }

    @Override
    public void deleteAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.removeTask(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        ArrayList<Integer> epicsList = new ArrayList<>(epics.keySet());
        for (int id : epicsList) {
            epics.get(id).clearSubtaskIds();
            updateEpicStatus(id);
        }
        for (int id : subtasks.keySet()) {
            historyManager.removeTask(id);
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        for (int id : epics.keySet()) {
            historyManager.removeTask(id);
        }
        epics.clear();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> epicSubs = epic.getSubtaskIds();
        if (epicSubs.isEmpty()) {
            epic.setStatus(NEW);
            return;
        }
        TaskStatus status = null;
        for (int id : epicSubs) {
            final Subtask subtask = subtasks.get(id);
            if (status == null) {
                status = subtask.getStatus();
                continue;
            }

            if (status.equals(subtask.getStatus()) && !status.equals(IN_PROGRESS)) {
                continue;
            }
            epic.setStatus(IN_PROGRESS);
            return;
        }

        epic.setStatus(status);

        for (int id : epicSubs) {
            final Subtask subtask = subtasks.get(id);
            if (status.equals(subtask.getStatus()) && !status.equals(DONE)) {
                continue;
            }
            epic.setStatus(DONE);
            return;
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistoryTask();
    }
}
