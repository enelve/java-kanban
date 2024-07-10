package test;

import historymanager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktype.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void newTaskAdded() {
        Task task = new Task(1, "Name", "Desciption", "NEW");
        historyManager.add(task);
        Task expectedTask = historyManager.getHistory().get(0);
        assertEquals(historyManager.getHistory().size(), 1);
        assertEquals(expectedTask.getName(), "Name");
        assertEquals(expectedTask.getDescription(), "Desciption");
        assertEquals(expectedTask.getStatus(), Task.TaskStatus.NEW);
    }

    @Test
    void taskIsShownOnlyOnce() {
        Task task = new Task(1, "Name", "Desciption", "NEW");
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(historyManager.getHistory().size(), 1);
    }

    @Test
    void taskIsRemoved() {
        Task task = new Task(1, "Name", "Desciption", "NEW");
        historyManager.add(task);
        historyManager.remove(task.getId());

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void lastOpenedTaskListedInTheEndOfHistory() {
        Task task1 = new Task(1, "Name", "Desciption", "NEW");
        Task task2 = new Task(2, "Name2", "Desciption3", "NEW");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(history.get(history.size() - 1).getId(), 1);
    }
}
