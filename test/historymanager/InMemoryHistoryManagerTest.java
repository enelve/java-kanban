package historymanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktype.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tasktype.Task.TaskType.TASK;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void newTaskAdded() {
        Task expectedTask = new Task(1, "Name", "Desciption", "NEW", TASK);
        historyManager.add(expectedTask);
        Task actualTask = historyManager.getHistory().get(0);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals("Name", actualTask.getName());
        assertEquals("Desciption", actualTask.getDescription());
        assertEquals(Task.TaskStatus.NEW, actualTask.getStatus());
    }

    @Test
    void taskIsShownOnlyOnce() {
        Task task = new Task(1, "Name", "Desciption", "NEW", TASK);
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void taskIsRemoved() {
        Task task = new Task(1, "Name", "Desciption", "NEW", TASK);
        historyManager.add(task);
        historyManager.remove(task.getId());

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void lastOpenedTaskListedInTheEndOfHistory() {
        Task task1 = new Task(1, "Name", "Desciption", "NEW", TASK);
        Task task2 = new Task(2, "Name2", "Desciption3", "NEW", TASK);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.get(history.size() - 1).getId());
    }
}
