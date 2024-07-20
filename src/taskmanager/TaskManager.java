package taskmanager;

import tasktype.Epic;
import tasktype.SubTask;
import tasktype.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    boolean createEpic(String name, String description);

    boolean createSubTask(String name, String description, String status, int epicId);

    boolean createTask(String name, String description, String status);

    boolean updateEpic(Epic epic);

    boolean updateTask(Task task);

    boolean updateSubTask(SubTask subTask);

    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpic(int id);

    List<Task> getHistory();

    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    List<SubTask> getEpicSubtasks(int id);

    void clearTasks();

    void clearSubTasks();

    void clearEpics();

    Set<Task> getPrioritizedTasks();
}
