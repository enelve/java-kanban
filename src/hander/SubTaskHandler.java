package hander;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.BadRequestException;
import exception.TimeConflictException;
import task.SubTask;
import task.Task;
import taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public SubTaskHandler(TaskManager manager) {
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
        if (userRequest.parameters().isEmpty()) {
            getAll(exchange);
        } else {
            int id = getIdFromQuery(userRequest.parameters());
            getById(exchange, id);
        }
    }

    private void getAll(HttpExchange exchange) throws IOException {
        sendText(exchange, jsonMapper.toJson(manager.getSubTasks()), 200);
    }

    private void getById(HttpExchange exchange, int id) throws IOException {
        Task subTask = manager.getSubTask(id);
        if (subTask != null) {
            sendText(exchange, jsonMapper.toJson(subTask), 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException, TimeConflictException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        SubTask subTask = jsonMapper.fromJson(body, SubTask.class);
        boolean hasTimeConfict = (subTask.getId() == 0)
                ? manager.createSubTask(subTask.getName(), subTask.getDescription(), subTask.getStatus().name(),
                subTask.getStartTime(), subTask.getDuration(), subTask.getEpicId())
                : manager.updateSubTask(subTask);
        if (hasTimeConfict) {
            throw new TimeConflictException(TIME_CONFLICT_MESSAGE);
        } else {
            sendText(exchange, HTTP_REQUEST_OK, 201);
        }
    }

    private void handleDelete(HttpExchange exchange, UserRequest userRequest) throws IOException {
        int id = getIdFromQuery(userRequest.parameters());
        manager.removeSubTask(id);
        sendText(exchange, HTTP_REQUEST_OK, 200);
    }
}

