package ru.homework.kanban.manager;

import ru.homework.kanban.tasks.Task;
import ru.homework.kanban.tasks.Subtask;
import ru.homework.kanban.tasks.Epic;
import ru.homework.kanban.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.homework.kanban.tasks.TaskStatus.NEW;
import static ru.homework.kanban.tasks.TaskStatus.IN_PROGRESS;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected int counterId = 0;
    protected final HistoryManager historyManager;
    private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime, Comparator.nullsFirst(Comparator.naturalOrder()));
    protected Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

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
        if (!checkIntersections(task)) {
            final int id = ++counterId;
            addToPrioritizedTasks(task);
            task.setId(id);
            tasks.put(id, task);
            return id;
        } else {
            System.out.println("Найдено пересечение во времени, задача не создана");
            return -1;
        }
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId()) && !checkIntersections(subtask)) {
            final int id = ++counterId;
            addToPrioritizedTasks(subtask);
            subtask.setId(id);
            subtasks.put(id, subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtaskId(id);
            updateEpicStatus(subtask.getEpicId());
            changeEpicTiming(epic);
            return id;
        } else {
            System.out.println("Найдено пересечение во времени, подзадача не создана");
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
            if (!checkIntersections(task)) {
                prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.equals(oldTask));
                addToPrioritizedTasks(task);
                tasks.put(task.getId(), task);
            } else {
                System.out.println("Найдено пересечение во времени, задача не изменена");
            }
        } else {
            System.out.println("Задача с ID " + task.getId() + " отсутствует!");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask != null) {
            if (!checkIntersections(subtask)) {
                int e = oldSubtask.getEpicId();
                epics.get(e).removeSubtaskId(subtask.getId());
                prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.equals(oldSubtask));
                addToPrioritizedTasks(subtask);
                subtasks.put(subtask.getId(), subtask);
                epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
                updateEpicStatus(e);
                updateEpicStatus(subtask.getEpicId());
                changeEpicTiming(epics.get(subtask.getEpicId()));
            } else {
                System.out.println("Найдено пересечение во времени, подзадача не изменена");
            }
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
            Task task = tasks.remove(id);
            historyManager.removeTask(id);
            prioritizedTasks.remove(task);
        } else {
            System.out.println("Задача с ID " + id + " отсутствует!");
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            int e = subtask.getEpicId();
            Epic epic = epics.get(e);
            epic.removeSubtaskId(id);
            updateEpicStatus(e);
            changeEpicTiming(epic);
            historyManager.removeTask(id);
            prioritizedTasks.remove(epic);
        } else {
            System.out.println("Подзадача с ID " + id + " отсутствует!");
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            for (int sub : epics.get(id).getSubtaskIds()) {
                Subtask subtask = subtasks.remove(sub);
                historyManager.removeTask(sub);
                prioritizedTasks.remove(subtask);
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
            prioritizedTasks.remove(tasks.get(id));
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
            prioritizedTasks.remove(subtasks.get(id));
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

    protected void changeEpicTiming(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setDuration(Duration.ofMinutes(0));
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }
        epic.setStartTime(subtasks.get(epic.getSubtaskIds().get(0)).getStartTime());
        epic.setEndTime(subtasks.get(epic.getSubtaskIds().get(0)).getEndTime());
        Duration epicDuration = Duration.ofMinutes(0);
        for (Integer id : epic.getSubtaskIds()) {
            LocalDateTime subtaskStart = subtasks.get(id).getStartTime();
            if (epic.getStartTime().isAfter(subtaskStart)) {
                epic.setStartTime(subtaskStart);
            }
            LocalDateTime subtaskEnd = subtasks.get(id).getEndTime();
            if (epic.getEndTime().isBefore((subtaskEnd))) {
                epic.setEndTime(subtaskEnd);
            }
            epicDuration = epicDuration.plus(subtasks.get(id).getDuration());
        }
        epic.setDuration(epicDuration);
    }

    protected void addToPrioritizedTasks(Task task) {
        prioritizedTasks.add(task);
    }

    protected boolean checkIntersections(Task task) {
        LocalDateTime startOfTask = task.getStartTime();
        LocalDateTime endOfTask = task.getEndTime();

        return prioritizedTasks.stream()
                .filter(prioritizedTask -> prioritizedTask.getStartTime() != null)
                .filter(prioritizedTask -> !prioritizedTask.equals(task))
                .anyMatch(prioritizedTask ->
                        !(prioritizedTask.getEndTime().isBefore(startOfTask) ||
                                endOfTask.isBefore(prioritizedTask.getStartTime()))
                );
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().collect(Collectors.toList());
    }

    public List<Task> getHistory() {
        return historyManager.getHistoryTask();
    }
}
