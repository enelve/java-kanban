package hander.util;

import com.google.gson.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class JsonMapper {
    private JsonMapper() {
    }

    public static Gson getDefaultJsonMapper() {
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
