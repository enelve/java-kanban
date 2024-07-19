package taskmanager;

import java.time.Duration;
import java.time.LocalDateTime;

public record TaskWorkLog(LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
}
