package ru.homework.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.homework.kanban.tasks.Subtask;
import ru.homework.kanban.manager.TaskManager;
import ru.homework.kanban.manager.ManagerException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHttpHandler extends BaseHttpHandler {
    public SubtaskHttpHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /Subtask запроса от клиента.");

        switch (httpExchange.getRequestMethod()) {
            case "POST":
                InputStream inputStream = httpExchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                if (!body.isEmpty()) {
                    Subtask postTask = gson.fromJson(body, Subtask.class);
                    if (postTask.getId() == 0) {
                        try {
                            manager.addNewSubtask(postTask);
                            writeResponse(httpExchange, "Подзадача создана.", 201);
                        } catch (ManagerException e) {
                            sendHasInteractions(httpExchange);
                        } catch (Exception e) {
                            sendServerError(httpExchange);
                        }
                    } else {
                        try {
                            manager.updateSubtask(postTask);
                            writeResponse(httpExchange, "Подзадача обновлена.", 201);
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
                        List<Subtask> subtasks = manager.getSubtasks();
                        String response = gson.toJson(subtasks);
                        sendText(httpExchange, response);
                    } catch (Exception e) {
                        sendServerError(httpExchange);
                    }
                } else {
                    try {
                        if (manager.getSubtask(id) != null) {
                            Subtask subtask = manager.getSubtask(id);
                            String response = gson.toJson(subtask);
                            sendText(httpExchange, response);
                        } else {
                            sendNotFound(httpExchange, "Подзадача с ID " + id + " отсутствует.");
                        }
                    } catch (Exception e) {
                        sendServerError(httpExchange);
                    }
                }
            case "DELETE":
                Integer deleteId = getIdFromPath(httpExchange.getRequestURI().getPath());
                try {
                    if (manager.getSubtask(deleteId) != null) {
                        manager.deleteSubtask(deleteId);
                        writeResponse(httpExchange, "Подзадача с ID " + deleteId + " удалена.", 200);
                    } else {
                        sendNotFound(httpExchange, "Подзадача с ID " + deleteId + " отсутствует. Уточните ID подзадачи и повторите запрос.");
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
