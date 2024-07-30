package hander;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.BadRequestException;
import exception.TimeConflictException;
import task.Epic;
import task.SubTask;
import taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
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
        if (userRequest.parameters().isEmpty() && userRequest.resources().isEmpty()) {
            getAll(exchange);
        } else {
            int id = getIdFromQuery(userRequest.parameters());
            if (userRequest.resources().contains("subtasks")) {
                getEpicSubtasks(exchange, id);
            } else {
                getEpicById(exchange, id);
            }
        }
    }

    private void getAll(HttpExchange exchange) throws IOException {
        sendText(exchange, jsonMapper.toJson(manager.getEpics()), 200);
    }

    private void getEpicById(HttpExchange exchange, int id) throws IOException {
        Epic epic = manager.getEpic(id);
        if (epic != null) {
            sendText(exchange, jsonMapper.toJson(epic), 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void getEpicSubtasks(HttpExchange exchange, int id) throws IOException {
        if (manager.getEpic(id) != null) {
            List<SubTask> subTasks = manager.getEpicSubtasks(id);
            sendText(exchange, jsonMapper.toJson(subTasks), 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException, TimeConflictException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Epic epic = jsonMapper.fromJson(body, Epic.class);
        if (epic.getId() == 0) {
            manager.createEpic(epic.getName(), epic.getDescription());
        } else {
            manager.updateEpic(epic);
        }
        sendText(exchange, HTTP_REQUEST_OK, 201);
    }

    private void handleDelete(HttpExchange exchange, UserRequest userRequest) throws IOException {
        int id = getIdFromQuery(userRequest.parameters());
        manager.removeEpic(id);
        sendText(exchange, HTTP_REQUEST_OK, 200);
    }
}


