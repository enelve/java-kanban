package test;

import historymanager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.InMemoryTaskManager;
import tasktype.Epic;
import tasktype.SubTask;
import tasktype.Task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void tasksEqualWhenIdSame() {
        Task task1 = new Task(1, "Test", "Test", "NEW");
        Task task2 = new Task(1, "Test2", "Test2", "DONE");
        assertEquals(task1, task2);
    }

    @Test
    void subTaskEqualWhenIdSame() {
        new Epic(1, "Test", "Test");
        new Epic(2, "Test", "Test");
        SubTask subTask1 = new SubTask(3, "Test", "Test", "NEW", 1);
        SubTask subTask2 = new SubTask(3, "Test2", "Test2", "NEW", 2);
        assertEquals(subTask1, subTask2);
    }

    @Test
    void epicsEqualWhenIdSame() {
        Epic epic1 = new Epic(1, "Test", "Test");
        Epic epic2 = new Epic(1, "Test2", "Test2");
        assertEquals(epic1, epic2);
    }

    @Test
    void subTaskNotCreatedWhenEpicNotExists() {
        taskManager.createSubTask("Test", "Test", "NEW", 99999);
        assertTrue(taskManager.getSubTasks().isEmpty());
    }

    @Test
    void taskCreatedAndFoundByID() {
        taskManager.createTask("TestName", "TestDescription", "NEW");
        Task task = taskManager.getTask(1);
        assertNotNull(task);
        assertEquals(task.getId(), 1);
        assertEquals(task.getName(), "TestName");
        assertEquals(task.getDescription(), "TestDescription");
        assertEquals(task.getStatus(), Task.TaskStatus.NEW);
    }

    @Test
    void subTaskCreatedAndFoundByID() {
        taskManager.createEpic("EpicName", "EpicDescription");
        taskManager.createSubTask("SubTaskName", "SubTaskDescription", "NEW", 1);
        SubTask subTask = taskManager.getSubTask(2);
        assertNotNull(subTask);
        assertEquals(subTask.getId(), 2);
        assertEquals(subTask.getName(), "SubTaskName");
        assertEquals(subTask.getDescription(), "SubTaskDescription");
        assertEquals(subTask.getStatus(), Task.TaskStatus.NEW);
    }

    @Test
    void epicCreatedAndFoundByID() {
        taskManager.createEpic("EpicName", "EpicDescription");
        Epic epic = taskManager.getEpic(1);
        assertNotNull(epic);
        assertEquals(epic.getId(), 1);
        assertEquals(epic.getName(), "EpicName");
        assertEquals(epic.getDescription(), "EpicDescription");
        assertEquals(epic.getStatus(), Task.TaskStatus.NEW);
    }

    @Test
    void taskAddedToHistoryAndNotChanged() {
        taskManager.createEpic("EpicName", "EpicDescription");
        Task expected = taskManager.getEpic(1);
        List<Task> historyList = taskManager.getHistory();
        assertEquals(1, historyList.size());
        Task actual = historyList.get(0);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    void historyStoresTenTasksOnly() {
        taskManager.createTask("Test", "Test", "DONE");
        for (int i = 1; i < 100; i++) {
            taskManager.getTask(1);
        }
        assertEquals(10, taskManager.getHistory().size());
    }

    @Test
    void idNotDuplicatedWhenTaskCreated() {
        for (int i = 1; i < 100; i++) {
            taskManager.createEpic("EpicName", "EpicDescription");
        }
        Set<Integer> idSet = new HashSet<>();
        for (Epic epic : taskManager.getEpics()) {
            idSet.add(epic.getId());
        }
        assertEquals(taskManager.getEpics().size(), idSet.size());
    }
}
