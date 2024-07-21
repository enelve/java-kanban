package historymanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static task.Task.TaskType.TASK;

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

    @Test
    void emptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void removeFromHistoryFirst() {
        Task task1 = new Task(1, "Name1", "Desciption1", "NEW", TASK);
        Task task2 = new Task(2, "Name2", "Desciption2", "NEW", TASK);
        Task task3 = new Task(3, "Name3", "Desciption3", "NEW", TASK);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);
        List<Task> actualHistory = historyManager.getHistory();
        assertEquals(2, actualHistory.get(0).getId());
        assertEquals(3, actualHistory.get(actualHistory.size() - 1).getId());
        assertEquals(2, actualHistory.size());
    }

    @Test
    void removeFromHistoryLast() {
        Task task1 = new Task(1, "Name1", "Desciption1", "NEW", TASK);
        Task task2 = new Task(2, "Name2", "Desciption2", "NEW", TASK);
        Task task3 = new Task(3, "Name3", "Desciption3", "NEW", TASK);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(3);
        List<Task> actualHistory = historyManager.getHistory();
        assertEquals(1, actualHistory.get(0).getId());
        assertEquals(2, actualHistory.get(actualHistory.size() - 1).getId());
        assertEquals(2, actualHistory.size());
    }

    @Test
    void removeFromHistoryMIddle() {
        Task task1 = new Task(1, "Name1", "Desciption1", "NEW", TASK);
        Task task2 = new Task(2, "Name2", "Desciption2", "NEW", TASK);
        Task task3 = new Task(3, "Name3", "Desciption3", "NEW", TASK);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2);
        List<Task> actualHistory = historyManager.getHistory();
        assertEquals(1, actualHistory.get(0).getId());
        assertEquals(3, actualHistory.get(actualHistory.size() - 1).getId());
        assertEquals(2, actualHistory.size());
    }
}
