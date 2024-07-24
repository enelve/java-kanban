package task;

import java.time.Duration;
import java.time.LocalDateTime;

import static task.Task.TaskType.SUBTASK;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(int id, String name, String description, String status, int epicId) {
        super(id, name, description, status, SUBTASK);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, String description, String status, LocalDateTime startTime,
                   Duration duration, int epicId) {
        super(id, name, description, status, SUBTASK, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        String parentString = super.toString();
        return parentString.substring(0, parentString.length() - 2) + ",," + epicId;
    }
}
