package ru.homework.kanban.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import ru.homework.kanban.manager.InMemoryTaskManager;
import ru.homework.kanban.manager.Managers;
import ru.homework.kanban.manager.TaskManager;
import ru.homework.kanban.tasks.Task;
import ru.homework.kanban.tasks.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpTaskServerTest")
public class HttpTaskServerTest {
    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Проверка создания задачи")
    public void testTaskCreate() throws IOException, InterruptedException {
        Task task = new Task("Test name", "Testing task description", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().minusDays(15));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = manager.getTasks();

        assertEquals(201, response.statusCode());
        assertNotNull(tasksFromManager, "Задача не создана");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test name", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Тест обновления задачи")
    public void testTaskUpdate() throws IOException, InterruptedException {
        Task task = new Task("Test task update", "", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().minusDays(15));
        manager.addNewTask(task);
        Task taskToServer = new Task(task.getId(), "Test task after update", "", TaskStatus.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.now().minusDays(10));

        String taskJson = gson.toJson(taskToServer);

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(),"Код ответа неверный");

        Task tasksFromManager = manager.getTask(1);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, manager.getTasks().size(), "Некорректное количество задач");
        assertEquals("Test task after update", tasksFromManager.getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Тест получения задачи по ID")
    public void testTaskGetTaskById() throws IOException, InterruptedException {
        manager.addNewTask (new Task(
                "Test get task by ID",
                "",
                TaskStatus.NEW,
                Duration.ofMinutes(0),
                LocalDateTime.now()));
        manager.addNewTask (new Task(
                "Test get task by ID",
                "",
                TaskStatus.NEW,
                Duration.ofMinutes(0),
                LocalDateTime.now().plusMinutes(5)));

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task gotTask = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Код ответа не верный");
        assertNotNull(gotTask, "Задачи не возвращаются");
        assertEquals("Test get task by ID", gotTask.getName(), "Некорректное имя переданной задачи");
    }

    @Test
    @DisplayName("Тест получения всех задач")
    public void testTaskGetAllTasks() throws IOException, InterruptedException {
        manager.addNewTask(new Task(
                "Test get all tasks",
                "",
                TaskStatus.NEW,
                Duration.ofMinutes(0),
                LocalDateTime.now()));
        manager.addNewTask(new Task(
                "Test get all tasks 2",
                "",
                TaskStatus.NEW,
                Duration.ofMinutes(0),
                LocalDateTime.now().plusMinutes(5)));

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> getAllResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> gotTasks = gson.fromJson(getAllResponse.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(200, getAllResponse.statusCode(), "Код ответа не верный");
        assertEquals(2, gotTasks.size(), "Некорректное количество задач");
    }

    @Test
    @DisplayName("Должно удалить задачу по ID")
    public void shouldDeleteTaskByIdTest() throws IOException, InterruptedException {
        manager.addNewTask(new Task(
                "Test delete task by ID",
                "",
                TaskStatus.NEW,
                Duration.ofMinutes(0),
                LocalDateTime.now()));
        manager.addNewTask(new Task(
                "Test delete task by ID 2",
                "",
                TaskStatus.NEW,
                Duration.ofMinutes(0),
                LocalDateTime.now().plusMinutes(5)));
        manager.addNewTask(new Task(
                "Test delete task by ID 3",
                "",
                TaskStatus.NEW,
                Duration.ofMinutes(0),
                LocalDateTime.now().plusMinutes(10)));
        manager.addNewTask(new Task(
                "Test delete task by ID 4",
                "",
                TaskStatus.NEW,
                Duration.ofMinutes(0),
                LocalDateTime.now().plusMinutes(15)));

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> deleteResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = List.of(manager.getTask(1), manager.getTask(3), manager.getTask(4));

        assertEquals(200, deleteResponse.statusCode());
        assertEquals(tasks, manager.getTasks(),"Задача не удалена");
    }

    @Test
    @DisplayName("Тест истории просмотров")
    public void historyTest() throws IOException, InterruptedException {
        Task task = new Task("", "", TaskStatus.NEW, Duration.ofMinutes(0), LocalDateTime.now());
        int taskId = manager.addNewTask(task);
        manager.getTask(taskId);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");

        HttpRequest getHistoryRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> getHistoryResponse = client.send(getHistoryRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getHistoryResponse.statusCode());
        List<Task> gotHistory = gson.fromJson(getHistoryResponse.body(), new TypeToken<List<Task>>(){}.getType());
        assertEquals(1, gotHistory.size(), "Передана некоректная история");
        assertEquals("", gotHistory.get(0).getName(), "Имя не совпадает");
    }

    @Test
    @DisplayName("Тест приоритизированных задач")
    public void prioritizedTest() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().minusDays(15));
        int taskId = manager.addNewTask(task);
        manager.getTask(taskId);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");

        HttpRequest getPrioritizedRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> getPrioritizedResponse = client.send(getPrioritizedRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getPrioritizedResponse.statusCode());
        List<Task> gotPrioritized = gson.fromJson(getPrioritizedResponse.body(), new TypeToken<List<Task>>(){}.getType());
        assertEquals(1, gotPrioritized.size(), "Некорректное количество задач в списке приоритета");
        assertEquals("Test 2", gotPrioritized.get(0).getName(), "Некорректное имя задачи");
    }
}
