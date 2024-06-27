package ru.homework.kanban.manager;

import ru.homework.kanban.tasks.Task;
import ru.homework.kanban.tasks.Subtask;
import ru.homework.kanban.tasks.Epic;
import ru.homework.kanban.tasks.TaskStatus;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import static ru.homework.kanban.tasks.TaskStatus.NEW;
import static ru.homework.kanban.tasks.TaskStatus.IN_PROGRESS;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected int counterId = 0;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            List<Subtask> subtasksEpic = new ArrayList<>();
            for (Integer sub : epic.getSubtaskIds()) {
                subtasksEpic.add(subtasks.get(sub));
            }
            return subtasksEpic;
        }
        return null;
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        if (task != null) {
            historyManager.addTask(task);
            return task;
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.addTask(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.addTask(epic);
            return epic;
        }
        return null;
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
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача с ID " + task.getId() + " отсутствует!");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask != null) {
            int e = oldSubtask.getEpicId();
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
        Epic oldEpic = epics.get(epic.getId());
        if (oldEpic != null) {
            oldEpic.setDescription(epic.getDescription());
            oldEpic.setName(epic.getName());
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
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            int e = subtask.getEpicId();
            epics.get(e).removeSubtaskId(id);
            updateEpicStatus(e);
            historyManager.removeTask(id);
        } else {
            System.out.println("Подзадача с ID " + id + " отсутствует!");
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            for (int sub : epics.get(id).getSubtaskIds()) {
                subtasks.remove(sub);
                historyManager.removeTask(sub);
            }
            epics.remove(id).clearSubtaskIds();
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
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic.getId());
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
        List<Integer> epicSubs = epic.getSubtaskIds();
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
    }

    public List<Task> getHistory() {
        return historyManager.getHistoryTask();
    }
}
