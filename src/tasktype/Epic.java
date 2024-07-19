package tasktype;

import java.util.ArrayList;
import java.util.List;

import static tasktype.Task.TaskType.EPIC;

public class Epic extends Task {
    private final List<Integer> subTasksId;

    public Epic(int id, String name, String description) {
        super(id, name, description, "NEW", EPIC);
        this.subTasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description, String status) {
        super(id, name, description, status, EPIC);
        this.subTasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description, String status, List<Integer> subTasksId) {
        super(id, name, description, status, EPIC);
        this.subTasksId = subTasksId;
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
}
