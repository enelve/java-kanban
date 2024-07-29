import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hander.util.JsonMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import taskmanager.Managers;
import taskmanager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static task.Task.TaskType.TASK;

class HttpTaskServerTest {
    private static final int TEST_DEFAULT_HTTP_PORT = 8081;
    TaskManager manager;
    HttpTaskServer httpTaskServer;
    HttpClient client;
    Gson jsonMapper;

    class SubTaskTypeToken extends TypeToken<List<SubTask>> {
    }

    class TaskTypeToken extends TypeToken<List<Task>> {
    }

    class EpicTypeToken extends TypeToken<List<Epic>> {
    }

    @BeforeEach
    void BeforeEach() throws IOException {
        manager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(manager, TEST_DEFAULT_HTTP_PORT);
        httpTaskServer.startHttpServer();

        client = HttpClient.newHttpClient();
        jsonMapper = JsonMapper.getDefaultJsonMapper();
    }

    @AfterEach
    void AfterEach() {
        httpTaskServer.stopHttpServer();
    }

    @Test
    void getTasksTest() throws IOException, InterruptedException {
        assertEquals(List.of(), manager.getTasks());
        manager.createTask("Task1", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(1));
        manager.createTask("Task2", "Description2", "DONE", LocalDateTime.now(), Duration.ofHours(1));
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(manager.getTasks(), jsonMapper.fromJson(response.body(), new TaskTypeToken().getType()));
    }

    @Test
    void getTaskByIdTest() throws IOException, InterruptedException {
        assertEquals(List.of(), manager.getTasks());
        manager.createTask("Task1", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(1));
        int taskId = manager.getTasks().get(0).getId();
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/tasks?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(manager.getTask(taskId), jsonMapper.fromJson(response.body(), Task.class));
    }

    @Test
    void getTaskByIdThatNotExistsTest() throws IOException, InterruptedException {
        assertEquals(List.of(), manager.getTasks());
        manager.createTask("Task1", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(1));
        int taskId = 2222;
        assertNotEquals(manager.getTasks().get(0).getId(), taskId);
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/tasks?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 404);
    }

    @Test
    void addNewTaskTest() throws IOException, InterruptedException {
        assertEquals(List.of(), manager.getTasks());
        Task newTask = new Task(0, "Task1", "Description", "NEW", TASK, LocalDateTime.now(), Duration.ofHours(1));
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.toJson(newTask)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        Task actual = manager.getTasks().get(0);
        assertEquals(actual.getName(), newTask.getName());
        assertEquals(actual.getDuration(), newTask.getDuration());
        assertEquals(actual.getStartTime(), newTask.getStartTime());
        assertEquals(actual.getDescription(), newTask.getDescription());
    }

    @Test
    void updateExistingTaskTest() throws IOException, InterruptedException {
        assertEquals(List.of(), manager.getTasks());
        manager.createTask("Task1", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(1));
        int existingTaskId = manager.getTasks().get(0).getId();
        Task modifiedTask = new Task(existingTaskId, "Task1", "Description New", "DONE", TASK,
                LocalDateTime.now(), Duration.ofHours(1));
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.toJson(modifiedTask)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task task = manager.getTasks().get(0);
        assertEquals(task.getName(), modifiedTask.getName());
        assertEquals(task.getDuration(), modifiedTask.getDuration());
        assertEquals(task.getStartTime(), modifiedTask.getStartTime());
        assertEquals(task.getDescription(), modifiedTask.getDescription());
    }

    @Test
    void addNewTaskHasTimeConflictTest() throws IOException, InterruptedException {
        assertEquals(List.of(), manager.getTasks());
        manager.createTask("Task1", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(1));
        Task newTask = new Task(0, "Task1", "Description", "NEW", TASK,
                LocalDateTime.now().plusMinutes(10), Duration.ofHours(1));
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.toJson(newTask)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    void deleteTaskByIdTest() throws IOException, InterruptedException {
        manager.createTask("Task1", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(1));
        int taskId = manager.getTasks().get(0).getId();
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/tasks?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(manager.getTask(taskId));
    }

    @Test
    void getSubTasksTest() throws IOException, InterruptedException {
        assertEquals(List.of(), manager.getSubTasks());
        manager.createEpic("Epic1", "Description");
        manager.createSubTask("SubTask1", "Description", "NEW", LocalDateTime.now(),
                Duration.ofHours(1), 1);
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(manager.getSubTasks(), jsonMapper.fromJson(response.body(), new SubTaskTypeToken().getType()));
    }

    @Test
    void getSubTaskByIdTest() throws IOException, InterruptedException {
        assertEquals(List.of(), manager.getSubTasks());
        manager.createEpic("Epic1", "Description");
        manager.createSubTask("SubTask1", "Description", "NEW", LocalDateTime.now(),
                Duration.ofHours(1), 1);
        int subTaskId = manager.getSubTasks().get(0).getId();
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/subtasks?id=" + subTaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(manager.getSubTask(subTaskId), jsonMapper.fromJson(response.body(), SubTask.class));
    }

    @Test
    void getSubTaskByIdThatNotExistsTest() throws IOException, InterruptedException {
        assertEquals(List.of(), manager.getSubTasks());
        manager.createEpic("Epic1", "Description");
        manager.createSubTask("SubTask1", "Description", "NEW", LocalDateTime.now(),
                Duration.ofHours(1), 1);
        int subTaskId = 2222;
        assertNotEquals(manager.getSubTasks().get(0).getId(), subTaskId);
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/subtasks?id=" + subTaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 404);
    }

    @Test
    void addNewSubTaskTest() throws IOException, InterruptedException {
        assertEquals(List.of(), manager.getSubTasks());
        manager.createEpic("Epic1", "Description");
        SubTask newSubTask = new SubTask(0, "SubTask1", "Description", "NEW",
                LocalDateTime.now(), Duration.ofHours(1), 1);
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.toJson(newSubTask)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        Task actual = manager.getSubTasks().get(0);
        assertEquals(actual.getName(), newSubTask.getName());
        assertEquals(actual.getDuration(), newSubTask.getDuration());
        assertEquals(actual.getStartTime(), newSubTask.getStartTime());
        assertEquals(actual.getDescription(), newSubTask.getDescription());
    }

    @Test
    void updateExistingSubTaskTest() throws IOException, InterruptedException {
        assertEquals(List.of(), manager.getSubTasks());
        manager.createEpic("Epic1", "Description");
        manager.createSubTask("SubTask1", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(1), 1);
        int existingSubTaskId = manager.getSubTasks().get(0).getId();
        Task modifiedSubTask = new SubTask(existingSubTaskId, "SubTask1", "Description New", "DONE", 1);
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.toJson(modifiedSubTask)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task subTask = manager.getSubTasks().get(0);
        assertEquals(subTask.getName(), modifiedSubTask.getName());
        assertEquals(subTask.getDuration(), modifiedSubTask.getDuration());
        assertEquals(subTask.getStartTime(), modifiedSubTask.getStartTime());
        assertEquals(subTask.getDescription(), modifiedSubTask.getDescription());
    }

    @Test
    void deleteSubTaskByIdTest() throws IOException, InterruptedException {
        assertEquals(List.of(), manager.getSubTasks());
        manager.createEpic("Epic1", "Description");
        manager.createSubTask("SubTask1", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(1), 1);
        int taskId = manager.getSubTasks().get(0).getId();
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/subtasks?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(manager.getSubTask(taskId));
    }

    @Test
    void getEpicsTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(manager.getEpics(), jsonMapper.fromJson(response.body(), new EpicTypeToken().getType()));
    }

    @Test
    void getEpicByIdTest() throws IOException, InterruptedException {
        manager.createEpic("Epic1", "Description");
        manager.createSubTask("SubTask1", "Description", "NEW", LocalDateTime.now(),
                Duration.ofHours(1), 1);
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/epics?id=" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(manager.getEpic(1), jsonMapper.fromJson(response.body(), Epic.class));
    }

    @Test
    void getEpicSubtasksByIdTest() throws IOException, InterruptedException {
        manager.createEpic("Epic1", "Description");
        manager.createSubTask("SubTask1", "Description", "NEW", LocalDateTime.now(),
                Duration.ofHours(1), 1);
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/epics/subtasks?id=" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(manager.getEpicSubtasks(1), jsonMapper.fromJson(response.body(), new SubTaskTypeToken().getType()));
    }

    @Test
    void addEpicTest() throws IOException, InterruptedException {
        Epic newEpic = new Epic(0, "Name", "Descrption");
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.toJson(newEpic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 201);
        assertEquals(newEpic.getName(), manager.getEpic(1).getName());
        assertEquals(newEpic.getDuration(), manager.getEpic(1).getDuration());
        assertEquals(newEpic.getStartTime(), manager.getEpic(1).getStartTime());
        assertEquals(newEpic.getDescription(), manager.getEpic(1).getDescription());
    }

    @Test
    void deleteEpicByIdTest() throws IOException, InterruptedException {
        manager.createEpic("Name", "Description");
        int epicId = manager.getEpics().get(0).getId();
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/epics?id=" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(manager.getEpic(epicId));
    }

    @Test
    void getPrioritizedTaskTest() throws IOException, InterruptedException {
        prepareData();
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> expected = manager.getPrioritizedTasks().stream().toList();
        List<Task> actual = jsonMapper.fromJson(response.body(), new TaskTypeToken().getType());
        assertEquals(expected, actual);
    }

    @Test
    void getHistoryTest() throws IOException, InterruptedException {
        prepareData();
        URI url = URI.create("http://localhost:" + TEST_DEFAULT_HTTP_PORT + "/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(manager.getHistory(), jsonMapper.fromJson(response.body(), new TaskTypeToken().getType()));
    }

    private void prepareData() {
        manager.createTask("Task1", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(1));
        manager.createTask("Task1", "Description", "NEW", LocalDateTime.now().plusMinutes(10), Duration.ofHours(1));
        manager.createTask("Task1", "Description", "NEW", LocalDateTime.now().plusDays(1), Duration.ofHours(1));
        manager.createTask("Task1", "Description", "NEW", LocalDateTime.now().plusHours(2), Duration.ofHours(1));
    }
}