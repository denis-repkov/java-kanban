package ru.homework.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.homework.kanban.tasks.Task;
import ru.homework.kanban.manager.TaskManager;
import ru.homework.kanban.manager.ManagerException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHttpHandler extends BaseHttpHandler {
    public TaskHttpHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /Task запроса от клиента.");

        switch (httpExchange.getRequestMethod()) {
            case "POST":
                InputStream inputStream = httpExchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                if (!body.isEmpty()) {
                    Task postTask = gson.fromJson(body, Task.class);
                    if (postTask.getId() == 0) {
                        try {
                            manager.addNewTask(postTask);
                            writeResponse(httpExchange, "Задача создана.", 201);
                        } catch (ManagerException e) {
                            sendHasInteractions(httpExchange);
                        } catch (Exception e) {
                            sendServerError(httpExchange);
                        }
                    } else {
                        try {
                            manager.updateTask(postTask);
                            writeResponse(httpExchange, "Задача обновлена.", 201);
                        } catch (ManagerException e) {
                            sendHasInteractions(httpExchange);
                        } catch (Exception e) {
                            sendServerError(httpExchange);
                        }
                    }
                } else {
                    throw new RuntimeException("Данные не переданы");
                }
            case "GET":
                Integer id = getIdFromPath(httpExchange.getRequestURI().getPath());
                if (id == null) {
                    try {
                        List<Task> tasks = manager.getTasks();
                        String response = gson.toJson(tasks);
                        sendText(httpExchange, response);
                    } catch (Exception e) {
                        sendServerError(httpExchange);
                    }
                } else {
                    try {
                        if (manager.getTask(id) != null) {
                            Task task = manager.getTask(id);
                            String response = gson.toJson(task);
                            sendText(httpExchange, response);
                        } else {
                            sendNotFound(httpExchange, "Задача с ID " + id + " отсутствует.");
                        }
                    } catch (Exception e) {
                        sendServerError(httpExchange);
                    }
                }
            case "DELETE":
                Integer deleteId = getIdFromPath(httpExchange.getRequestURI().getPath());
                try {
                    if (manager.getTask(deleteId) != null) {
                        manager.deleteTask(deleteId);
                        writeResponse(httpExchange, "Задача с ID " + deleteId + " удалена.", 200);
                    } else {
                        sendNotFound(httpExchange, "Задача с ID " + deleteId + " отсутствует. Уточните ID задачи и повторите запрос.");
                    }
                } catch (Exception e) {
                    sendServerError(httpExchange);
                }
            default:
                try {
                    sendNotFound(httpExchange, "Такого запроса не существует");
                } catch (Exception e) {
                    sendServerError(httpExchange);
                }
        }
    }
}
