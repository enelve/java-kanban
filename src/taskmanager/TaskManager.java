package taskmanager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    void createEpic(String name, String description);

    boolean createSubTask(String name, String description, String status, LocalDateTime startTime,
                          Duration duration, int epicId);

    boolean createTask(String name, String description, String status, LocalDateTime startTime, Duration duration);

    void updateEpic(Epic epic);

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

    List<Task> getAll();

    List<SubTask> getEpicSubtasks(int id);

    void clearTasks();

    void clearSubTasks();

    void clearEpics();

    Set<Task> getPrioritizedTasks();
}
