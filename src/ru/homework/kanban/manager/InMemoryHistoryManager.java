package ru.homework.kanban.manager;

import ru.homework.kanban.tasks.Task;

import java.util.List;
import java.util.LinkedList;
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
        if (node != null) {
            final Node prev = node.prev;
            final Node next = node.next;
            if (prev != null && next != null) { // для середины
                prev.next = next;
                next.prev = prev;
            } else if (prev == null && next != null) { //для головы
                first = next;
                next.prev = null;
            } else if (prev != null) { // для хвоста
                last = prev;
                prev.next = null;
            } else {
                first = null;
                last = null;
            }
        }
    }

    private Node linkLast(Task task) {
        final Node node = new Node(task, last, null);
        if (last == null) {
            first = node;
        } else {
            last.next = node;
            node.prev = last;
        }
        last = node;
        return node;
    }

    private LinkedList<Task> getTasks() {
        LinkedList<Task> historyTask = new LinkedList<>();
        Node node = first;
        while (node != null) {
            historyTask.add(node.task);
            node = node.next;
        }
        return historyTask;
    }
}
