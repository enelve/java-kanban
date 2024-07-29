import com.sun.net.httpserver.HttpServer;
import hander.*;
import taskmanager.Managers;
import taskmanager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final TaskManager manager;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager, int port) throws IOException {
        this.manager = manager;
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager, 8080);
        httpTaskServer.startHttpServer();


        manager.createEpic("Epic1", "Description");
        manager.createTask("Task1", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(1));
        manager.createSubTask("SubTask1", "Description", "NEW", LocalDateTime.now().minusDays(1), Duration.ofHours(1), 1);
        manager.createTask("Task2", "Description", "NEW", LocalDateTime.now().minusDays(2), Duration.ofHours(1));
        manager.createEpic("Epic2", "Description");
        manager.createSubTask("SubTask2", "Description", "NEW", LocalDateTime.now().plusDays(2), Duration.ofHours(1), 1);

    }

    public void stopHttpServer() {
        this.httpServer.stop(0);
    }

    private void startHttpServer() {
        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/subtasks", new SubTaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
        httpServer.start();
        System.out.println("Сервер успешно запущен");
    }
}
