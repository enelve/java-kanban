package taskmanager;

import tasktype.Epic;
import tasktype.SubTask;
import tasktype.Task;

import java.util.List;

public interface TaskManager {
    void createEpic(String name, String description);

    void createSubTask(String name, String description, String status, int epicId);

    void createTask(String name, String description, String status);

    void updateEpic(Epic epic);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpic(int id);

    List<Task> getHistory();

    public List<Task> getTasks();

    public List<SubTask> getSubTasks();

    public List<Epic> getEpics();

    public List<SubTask> getEpicSubtasks(int id);
}
