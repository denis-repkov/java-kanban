package ru.homework.kanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.homework.kanban.manager.InMemoryTaskManager;
import ru.homework.kanban.manager.Managers;
import ru.homework.kanban.manager.TaskManager;
import ru.homework.kanban.http.adapter.DurationAdapter;
import ru.homework.kanban.http.adapter.LocalDateTimeAdapter;
import ru.homework.kanban.http.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    public TaskManager manager;
    private Gson gson;


    public HttpTaskServer(TaskManager manager) {
        try {
            this.manager = manager;
            gson = getGson();
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/tasks", new TaskHttpHandler(manager, gson));
            server.createContext("/subtasks", new SubtaskHttpHandler(manager, gson));
            server.createContext("/epics", new EpicHttpHandler(manager, gson));
            server.createContext("/history", new HistoryHttpHandler(manager, gson));
            server.createContext("/prioritized", new PrioritizedHttpHandler(manager, gson));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer(new InMemoryTaskManager(Managers.getDefaultHistory()));
        taskServer.start();
    }

    public void start() {
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        server.start();
    }

    public void stop() {
        System.out.println("HTTP-сервер на " + PORT + " порту был остановлен!");
        server.stop(1);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }
}
