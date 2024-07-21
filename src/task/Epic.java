package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static task.Task.TaskType.EPIC;

public class Epic extends Task {
    private final List<Integer> subTasksId;
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description, "NEW", EPIC);
        this.subTasksId = new ArrayList<>();
        this.endTime = null;
    }

    public Epic(int id, String name, String description, String status,
                LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(id, name, description, status, EPIC, startTime, duration);
        this.subTasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description, String status, List<Integer> subTasksId,
                LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(id, name, description, status, EPIC, startTime, duration);
        this.subTasksId = subTasksId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public List<Integer> getSubTasksId() {
        return subTasksId;
    }

    public void linkSubTaskToEpic(int id) {
        if (!subTasksId.contains(id)) {
            subTasksId.add(id);
        }
    }

    public void unlinkSubTaskFromEpic(int id) {
        if (subTasksId.contains(id)) {
            subTasksId.remove((Integer) id);
        }
    }

    @Override
    public String toString() {
        String parentString = super.toString();
        return parentString.substring(0, parentString.length() - 2) + "," +
                Optional.ofNullable(this.getStartTime()).map(LocalDateTime::toString).orElse("") + ',';
    }
}
