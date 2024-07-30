package hander;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.BadRequestException;
import exception.TimeConflictException;
import task.Task;
import taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            System.out.println("Получен http запрос");
            UserRequest userRequest = parseUserRequest(exchange);
            switch (userRequest.type()) {
                case POST -> handlePut(exchange);
                case DELETE -> handleDelete(exchange, userRequest);
                case GET -> handleGet(exchange, userRequest);
                default -> throw new BadRequestException("Неизвестный тип запроса");
            }
        } catch (BadRequestException e) {
            sendText(exchange, e.getMessage(), 400);
        } catch (TimeConflictException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            System.out.println("Ошибка : " + e.getMessage());
            sendText(exchange, e.getMessage(), 500);
        }
    }

    private void handleGet(HttpExchange exchange, UserRequest userRequest) throws IOException {
        if (userRequest.parameters().isEmpty()) {
            getAll(exchange);
        } else {
            int id = getIdFromQuery(userRequest.parameters());
            getById(exchange, id);
        }
    }

    private void getAll(HttpExchange exchange) throws IOException {
        sendText(exchange, jsonMapper.toJson(manager.getTasks()), 200);
    }

    private void getById(HttpExchange exchange, int id) throws IOException {
        Task task = manager.getTask(id);
        if (task != null) {
            sendText(exchange, jsonMapper.toJson(task), 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException, TimeConflictException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Task task = jsonMapper.fromJson(body, Task.class);
        boolean hasTimeConfict = (task.getId() == 0)
                ? manager.createTask(task.getName(), task.getDescription(), task.getStatus().name(),
                task.getStartTime(), task.getDuration())
                : manager.updateTask(task);
        if (hasTimeConfict) {
            throw new TimeConflictException(TIME_CONFLICT_MESSAGE);
        } else {
            sendText(exchange, HTTP_REQUEST_OK, 201);
        }
    }

    private void handleDelete(HttpExchange exchange, UserRequest userRequest) throws IOException {
        int id = getIdFromQuery(userRequest.parameters());
        manager.removeTask(id);
        sendText(exchange, HTTP_REQUEST_OK, 200);
    }
}

