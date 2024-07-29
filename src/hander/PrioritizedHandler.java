package hander;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.BadRequestException;
import taskmanager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        super();
        this.manager = manager;
    }

    public void handle(HttpExchange exchange) throws IOException {
        UserRequest userRequest = parseUserRequest(exchange);
        switch (userRequest.type()) {
            case GET -> handleGet(exchange);
            default -> throw new BadRequestException("Неизвестный тип запроса");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        sendText(exchange, jsonMapper.toJson(manager.getPrioritizedTasks()), 200);
    }
}
