package ru.homework.kanban.manager;

import ru.homework.kanban.tasks.Task;
import ru.homework.kanban.tasks.Subtask;
import ru.homework.kanban.tasks.Epic;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int counterId = 0;

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

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

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public int addNewTask(Task task) {
        final int id = ++counterId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

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

    public int addNewEpic(Epic epic) {
        final int id = ++counterId;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача с ID " + task.getId() + " отсутствует!");
        }
    }

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

    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задача с ID " + id + " отсутствует!");
        }
    }

    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int e = subtasks.get(id).getEpicId();
            epics.get(e).removeSubtaskId(id);
            subtasks.remove(id);
            updateEpicStatus(e);
        } else {
            System.out.println("Подзадача с ID " + id + " отсутствует!");
        }
    }

    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subtasksEpic = new ArrayList<>(epics.get(id).getSubtaskIds());
            for (int sub : subtasksEpic) {
                subtasks.remove(sub);
            }
            epics.get(id).clearSubtaskIds();
            epics.remove(id);
        } else {
            System.out.println("Эпик с ID " + id + " отсутствует!");
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        ArrayList<Integer> epicsList = new ArrayList<>(epics.keySet());
        for (int id : epicsList) {
            epics.get(id).clearSubtaskIds();
            updateEpicStatus(id);
        }
        subtasks.clear();
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> epicSubs = epic.getSubtaskIds();
        if (epicSubs.isEmpty()) {
            epic.setStatus("NEW");
            return;
        }
        String status = null;
        for (int id : epicSubs) {
            final Subtask subtask = subtasks.get(id);
            if (status == null) {
                status = subtask.getStatus();
                continue;
            }

            if (status.equals(subtask.getStatus()) && !status.equals("IN_PROGRESS")) {
                continue;
            }
            epic.setStatus("IN_PROGRESS");
            return;
        }

        epic.setStatus(status);

        for (int id : epicSubs) {
            final Subtask subtask = subtasks.get(id);
            if (status.equals(subtask.getStatus()) && !status.equals("DONE")) {
                continue;
            }
            epic.setStatus("DONE");
            return;
        }
    }
}
