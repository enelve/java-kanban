package taskmanager;

import historymanager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static task.Task.TaskStatus.NEW;

class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void tasksEqualWhenIdSame() {
        Task task1 = new Task(1, "Test", "Test", "NEW", Task.TaskType.TASK);
        Task task2 = new Task(1, "Test2", "Test2", "DONE", Task.TaskType.TASK);
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
        assertEquals(1, task.getId());
        assertEquals("TestName", task.getName());
        assertEquals("TestDescription", task.getDescription());
        assertEquals(NEW, task.getStatus());
    }

    @Test
    void subTaskCreatedAndFoundByID() {
        taskManager.createEpic("EpicName", "EpicDescription");
        taskManager.createSubTask("SubTaskName", "SubTaskDescription", "NEW", 1);
        SubTask subTask = taskManager.getSubTask(2);
        assertNotNull(subTask);
        assertEquals(2, subTask.getId());
        assertEquals("SubTaskName", subTask.getName());
        assertEquals("SubTaskDescription", subTask.getDescription());
        assertEquals(NEW, subTask.getStatus());
    }

    @Test
    void epicCreatedAndFoundByID() {
        taskManager.createEpic("EpicName", "EpicDescription");
        Epic epic = taskManager.getEpic(1);
        assertNotNull(epic);
        assertEquals(1, epic.getId());
        assertEquals("EpicName", epic.getName());
        assertEquals("EpicDescription", epic.getDescription());
        assertEquals(NEW, epic.getStatus());
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
    void historyDoesNotStoreDuplicates() {
        taskManager.createTask("Test", "Test", "DONE");
        for (int i = 1; i < 100; i++) {
            taskManager.getTask(1);
        }
        assertEquals(1, taskManager.getHistory().size());
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

    @Test
    void epicAndCorrespondingSubtasksNotShowmInHistoryAfterDeletion() {
        taskManager.createEpic("EpicName", "EpicDescription");
        taskManager.createSubTask("SubTaskName", "SubTaskDescription", "NEW", 1);
        taskManager.getEpic(1);
        taskManager.getSubTask(2);
        assertEquals(2, taskManager.getHistory().size());

        taskManager.removeEpic(1);
        assertEquals(0, taskManager.getHistory().size());
    }
}
