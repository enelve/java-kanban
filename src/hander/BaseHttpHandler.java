package hander;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import exception.BadRequestException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BaseHttpHandler {
    protected static final String HTTP_REQUEST_OK = "Запрос успешно обработан";
    protected static final String TIME_CONFLICT_MESSAGE = "Обнаружен конфликт по времени выполнения с существующей задачей";
    protected final Gson jsonMapper;

    public BaseHttpHandler(Gson jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public BaseHttpHandler() {
        this.jsonMapper = getDefaultJsonMapper();
    }

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    record UserRequest(RequestType type, List<String> resources, Map<String, String> parameters) {
    }

    protected void sendText(HttpExchange exchange, String text, int code) throws IOException {
        if (text.isBlank()) {
            exchange.sendResponseHeaders(code, 0);
        } else {
            byte[] bytes = text.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(code, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "Задача c указанным номером не найдена", 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, "Конфликт с существующей задачей по времени выполнения", 406);
    }

    protected UserRequest parseUserRequest(HttpExchange exchange) {
        RequestType type = RequestType.valueOf(exchange.getRequestMethod());
        List<String> resources = Arrays.stream(exchange.getRequestURI().getPath().split("/"))
                .skip(2)
                .filter(resource -> !resource.contains("?"))
                .toList();
        Map<String, String> parameters = new HashMap<>();
        Optional.ofNullable(exchange.getRequestURI().getQuery())
                .map(query -> Arrays.stream(query.split("&")).toList())
                .orElse(List.of())
                .forEach(parameter -> {
                    String[] pair = parameter.split("=");
                    parameters.put(pair[0], pair[1]);
                });

        return new UserRequest(type, resources, parameters);
    }

    protected int getIdFromQuery(Map<String, String> parameters) {
        try {
            return Optional.ofNullable(parameters.get("id"))
                    .map(Integer::valueOf)
                    .orElseThrow(() -> new BadRequestException("Id не передан в запросе"));
        } catch (NumberFormatException e) {
            throw new BadRequestException("Некорректно задан ID сделки - ожидается целое число");
        }
    }

    protected Gson getDefaultJsonMapper() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (value, type, context) ->
                        new JsonPrimitive(value.format(DateTimeFormatter.ISO_DATE_TIME))
                )
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (jsonElement, type, context) ->
                        LocalDateTime.parse(jsonElement.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_DATE_TIME)
                )
                .registerTypeAdapter(Duration.class, (JsonSerializer<Duration>) (value, type, context) ->
                        new JsonPrimitive(value.toMinutes())
                )
                .registerTypeAdapter(Duration.class, (JsonDeserializer<Duration>) (jsonElement, type, context) ->
                        Duration.ofMinutes(jsonElement.getAsJsonPrimitive().getAsInt())
                )
                .create();
    }
}
