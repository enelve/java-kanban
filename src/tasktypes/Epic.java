package tasktypes;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasksId;

    public Epic(int id, String name, String description) {
        super(id, name, description, "NEW");
        this.subTasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description, String status, List<Integer> subTasksId) {
        super(id, name, description, status);
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

    @Override
    public String toString() {
        return super.toString() + ", SubTasks id=" + subTasksId;
    }
}
