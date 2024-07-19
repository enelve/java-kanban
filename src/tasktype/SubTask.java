package tasktype;

import static tasktype.Task.TaskType.SUBTASK;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(int id, String name, String description, String status, int epicId) {
        super(id, name, description, status, SUBTASK);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return super.toString() + epicId;
    }
}
