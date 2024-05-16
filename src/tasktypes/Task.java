package tasktypes;

import java.util.Objects;

public class Task {
    private final int id;
    private final TaskStatus status;
    private final String name;
    private final String description;

    public enum TaskStatus{
        NEW, IN_PROGRESS, DONE
    }

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.description = description;
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
        return "id=" + id + ", name=" + name + ", description=" + description + ", status=" + status;
    }
}
