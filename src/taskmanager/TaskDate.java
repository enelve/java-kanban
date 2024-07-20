package taskmanager;

import java.time.Duration;
import java.time.LocalDateTime;

public record TaskDate(LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
}
