package ru.homework.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.homework.kanban.manager.ManagerException;
import ru.homework.kanban.tasks.Epic;
import ru.homework.kanban.tasks.Subtask;
import ru.homework.kanban.manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHttpHandler extends BaseHttpHandler {

    public EpicHttpHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /Epic запроса от клиента.");

        switch (httpExchange.getRequestMethod()) {
            case "POST":
                try (InputStream inputStream = httpExchange.getRequestBody()) {
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    if (!body.isEmpty()) {
                        Epic postTask = gson.fromJson(body, Epic.class);
                        if (postTask.getId() == 0) {
                            try {
                                manager.addNewEpic(postTask);
                                writeResponse(httpExchange, "Эпик создан.", 201);
                            } catch (ManagerException e) {
                                sendHasInteractions(httpExchange);
                            } catch (Exception e) {
                                sendServerError(httpExchange);
                            }
                        } else {
                            try {
                                manager.updateEpic(postTask);
                                writeResponse(httpExchange, "Эпик обновлен.", 201);
                            } catch (ManagerException e) {
                                sendHasInteractions(httpExchange);
                            } catch (Exception e) {
                                sendServerError(httpExchange);
                            }
                        }
                    } else {
                        throw new RuntimeException("Данные не переданы");
                    }
                }
            case "GET":
                Integer id = getIdFromPath(httpExchange.getRequestURI().getPath());
                String subPath = getSubPathFromPath(httpExchange.getRequestURI().getPath());
                if (id == null) {
                    try {
                        List<Epic> epics = manager.getEpics();
                        String response = gson.toJson(epics);
                        sendText(httpExchange, response);
                    } catch (Exception e) {
                        sendServerError(httpExchange);
                    }
                } else {
                    try {
                        if (manager.getEpic(id) != null) {
                            if (subPath != null) {
                                List<Subtask> epicsSubtasks = manager.getEpicSubtasks(id);
                                String response = gson.toJson(epicsSubtasks);
                                sendText(httpExchange, response);
                            } else {
                                if (manager.getEpic(id) != null) {
                                    Epic epic = (Epic) manager.getEpic(id);
                                    String response = gson.toJson(epic);
                                    sendText(httpExchange, response);
                                } else {
                                    sendNotFound(httpExchange, "Эпик с ID " + id + " отсутствует.");
                                }
                            }
                        }
                    } catch (Exception e) {
                        sendServerError(httpExchange);
                    }
                }
            case "DELETE":
                Integer deleteId = getIdFromPath(httpExchange.getRequestURI().getPath());
                try {
                    if (manager.getEpic(deleteId) != null) {
                        manager.deleteEpic(deleteId);
                        writeResponse(httpExchange, "Эпик с ID " + deleteId + " удален.", 200);
                    } else {
                        sendNotFound(httpExchange, "Эпик с ID " + deleteId + " отсутствует. Уточните ID эпика и повторите запрос.");
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
