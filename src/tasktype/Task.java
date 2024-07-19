package tasktype;

import exception.TaskParsingFromStringException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    private final int id;
    private final TaskStatus status;
    private final String name;
    private final String description;
    private final TaskType taskType;
    private final Duration duration;
    private final LocalDateTime startTime;

    public enum TaskStatus {
        NEW, IN_PROGRESS, DONE
    }

    public enum TaskType {
        TASK, SUBTASK, EPIC;
    }

    public Task(int id, String name, String description, String status, TaskType taskType, LocalDateTime startTime,
                Duration duration) {
        this.id = id;
        this.status = Task.TaskStatus.valueOf(status);
        this.name = name;
        this.description = description;
        this.taskType = taskType;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, String description, String status, TaskType taskType) {
        this.id = id;
        this.status = Task.TaskStatus.valueOf(status);
        this.name = name;
        this.description = description;
        this.taskType = taskType;
        this.startTime = null;
        this.duration = null;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public static Task fromString(String value) {

        String[] split = value.split(",", -1);
        if (split.length == 0) {
            return null;
        } else if (split.length < 8) {
            throw new TaskParsingFromStringException("Недостаточно данных для загрузки списка задач");
        }
        int id = Integer.parseInt(split[0]);
        TaskType taskType = TaskType.valueOf(split[1]);
        String name = split[2];
        String status = split[3];
        String description = split[4];
        LocalDateTime startTime = (!split[5].isEmpty()) ? LocalDateTime.parse(split[5]) : null;
        Duration duration = Duration.ofMinutes(Long.parseLong(split[6]));

        switch (taskType) {
            case TASK:
                return new Task(id, name, description, status, taskType, startTime, duration);
            case EPIC:
                LocalDateTime endTime = (!split[7].isEmpty()) ? LocalDateTime.parse(split[7]) : null;
                return new Epic(id, name, description, status, startTime, duration, endTime);
            case SUBTASK:
                int epic = Integer.parseInt(split[8]);
                return new SubTask(id, name, description, status, startTime, duration, epic);
            default:
                throw new TaskParsingFromStringException("Неизвестный тип задачи");
        }
    }

    public LocalDateTime getEndTime() {
        LocalDateTime result = null;
        if (startTime != null && duration != null) {
            result = startTime.plus(duration);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%d,,", id, taskType, name, status, description,
                Optional.ofNullable(this.getStartTime()).map(LocalDateTime::toString).orElse(""),
                Optional.ofNullable(duration).map(Duration::toMinutes).orElse(0L));
    }
}
