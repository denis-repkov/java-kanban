package ru.homework.kanban.manager;

import ru.homework.kanban.tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {


    private File file = new File("resources", "backupTasks.csv");
    private static final String HEADER_CSV_FILE = "id,type,name,status,description,epic\n";

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        int maxID = 0;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isEmpty()) {
                    break;
                }

                Task task = fromString(line);
                if (task.getId() > maxID) {
                    maxID = task.getId();
                }
                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        break;
                    case SUBTASK:
                        manager.subtasks.put(task.getId(), (Subtask) task);
                        break;
                    case EPIC:
                        manager.epics.put(task.getId(), (Epic) task);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerException("Ошибка при считывании данных из файла!");
        }
        for (Subtask st : manager.subtasks.values()) {
            manager.epics.get(st.getEpicId()).addSubtaskId(st.getId());
        }
        manager.counterId = (maxID);
        return manager;
    }

    public void save() {
        try {
            if (!Files.exists(file.toPath())) {
                Files.createFile(file.toPath());
            }
        } catch (IOException e) {
            throw new ManagerException("Отсутствует файл для записи данных!");
        }

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER_CSV_FILE);
            for (Task task : getTasks()) {
                writer.write(task.toFileString() + "\n");
            }

            for (Epic epic : getEpics()) {
                writer.write(epic.toFileString() + "\n");
            }

            for (Subtask subtask : getSubtasks()) {
                writer.write(subtask.toFileString() + "\n");
            }
        } catch (ManagerException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Task fromString(String value) {
        String[] file = value.split(",");
        int id = Integer.parseInt(file[0]);
        String type = file[1];
        String title = file[2];
        TaskStatus status = TaskStatus.valueOf(file[3].toUpperCase());
        String description = file[4];
        Integer subsEpic = null;
        if (type.equals("SUBTASK")) {
            subsEpic = Integer.parseInt(file[5]);
        }
        if (type.equals("EPIC")) {
            Epic epic = new Epic(id, title, description);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else if (type.equals("SUBTASK")) {
            Subtask subtask = new Subtask(id, title, description, status, subsEpic);
            subtask.setId(id);
            return subtask;
        } else {
            Task task = new Task(id, title, description, status);
            task.setId(id);
            return task;
        }
    }

    @Override
    public int addNewTask(Task task) {
        super.addNewTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addNewSubtask(Subtask subTask) {
        super.addNewSubtask(subTask);
        save();
        return subTask.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }


    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    public void updateSubtask(Subtask subTask) {
        super.updateSubtask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }
}
