package tasktype;

import exception.TaskParsingFromStringException;

import java.util.Objects;

public class Task {
    private final int id;
    private final TaskStatus status;
    private final String name;
    private final String description;
    private final TaskType taskType;

    public enum TaskStatus {
        NEW, IN_PROGRESS, DONE
    }

    public enum TaskType {
        TASK, SUBTASK, EPIC;
    }

    public Task(int id, String name, String description, String status, TaskType taskType) {
        this.id = id;
        this.status = Task.TaskStatus.valueOf(status);
        this.name = name;
        this.description = description;
        this.taskType = taskType;
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

    public static Task fromString(String value) {
        String[] split = value.split(",", -1);
        if (split.length == 0) {
            return null;
        } else if (split.length < 6) {
            throw new TaskParsingFromStringException("Недостаточно данных для загрузки списка задач");
        }
        int id = Integer.parseInt(split[0]);
        TaskType taskType = TaskType.valueOf(split[1]);
        String name = split[2];
        String status = split[3];
        String description = split[4];

        switch (taskType) {
            case TASK:
                return new Task(id, name, description, status, taskType);
            case EPIC:
                return new Epic(id, name, description, status);
            case SUBTASK:
                int epic = Integer.parseInt(split[5]);
                return new SubTask(id, name, description, status, epic);
            default:
                throw new TaskParsingFromStringException("Неизвестный тип задачи");
        }
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
        return String.format("%d,%s,%s,%s,%s,", id, taskType, name, status, description);
    }
}
