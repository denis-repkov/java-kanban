package ru.homework.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.homework.kanban.tasks.Task;
import ru.homework.kanban.manager.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHttpHandler extends BaseHttpHandler {
    public HistoryHttpHandler(TaskManager manager, Gson gson) {
        super(manager,gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /History запроса от клиента.");
        try {
            if (httpExchange.getRequestMethod().equals("GET")) {
                List<Task> history = manager.getHistory();
                String response = gson.toJson(history);
                sendText(httpExchange, response);
            } else {
                sendNotFound(httpExchange, "Такого запроса не существует");
            }
        } catch (Exception e) {
            sendServerError(httpExchange);
        }
    }
}
