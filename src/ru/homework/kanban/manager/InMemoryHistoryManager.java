package ru.homework.kanban.manager;

import ru.homework.kanban.tasks.Task;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "task=" + task +
                    ", prev=" + prev +
                    ", next=" + next +
                    '}';
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;


    @Override
    public List<Task> getHistoryTask() {
        return getTasks();
    }

    @Override
    public void addTask(Task task) {
        int taskId = task.getId();
        if (nodeMap.containsKey(taskId)) {
            removeTask(taskId);
        }
        nodeMap.put(taskId, linkLast(task));
    }

    @Override
    public void removeTask(int id) {
        Node node = nodeMap.remove(id);
        if (node.prev != null && node.next != null) { // для середины
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } else if (node.prev == null && node.next != null) { //для головы
            first = node.next;
            node.next.prev = null;
        } else if (node.prev != null) { // для хвоста
            last = node.prev;
            node.prev.next = null;
        } else {
            first = null;
            last = null;
        }
    }

    private Node linkLast(Task task) {
        final Node node = new Node(task, last, null);
        if (last == null) {
            first = node;
        } else {
            if (first.next == null) {
                first.next = node;
            }
            last.next = node;
            node.prev = last;
        }
        last = node;
        return node;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> historyTask = new ArrayList<>();
        Node node = first;
        while (node != null) {
            historyTask.add(node.task);
            node = node.next;
        }
        return historyTask;
    }
}
