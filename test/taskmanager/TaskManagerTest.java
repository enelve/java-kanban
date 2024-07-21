package taskmanager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import task.Epic;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static task.Task.TaskStatus.NEW;
import static task.Task.TaskType.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

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
        taskManager.createSubTask("Test", "Test", "NEW", LocalDateTime.now(), Duration.ofHours(20),
                99999);
        assertTrue(taskManager.getSubTasks().isEmpty());
    }

    @Test
    void taskCreatedAndFoundByID() {
        taskManager.createTask("TestName", "TestDescription", "NEW", LocalDateTime.now(),
                Duration.ofHours(20));
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
        taskManager.createSubTask("SubTaskName", "SubTaskDescription", "NEW", LocalDateTime.now(),
                Duration.ofHours(20), 1);
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
        taskManager.createTask("Test", "Test", "DONE", LocalDateTime.now(), Duration.ofHours(20));
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
        taskManager.createSubTask("SubTaskName", "SubTaskDescription", "NEW", LocalDateTime.now(),
                Duration.ofHours(20), 1);
        taskManager.getEpic(1);
        taskManager.getSubTask(2);
        assertEquals(2, taskManager.getHistory().size());

        taskManager.removeEpic(1);
        assertEquals(0, taskManager.getHistory().size());
    }

    @ParameterizedTest
    @MethodSource("getEpicStatusTestData")
    void calculateEpicStatusTest(TestHelper.EpicStatusTestData epicStatusTestData) {
        taskManager.createEpic("Test Name", "Test Description");
        int epicId = taskManager.getEpics().get(0).getId();
        epicStatusTestData.subTaskList().forEach(subTask -> {
            taskManager.createSubTask(subTask.getName(), subTask.getDescription(), subTask.getStatus().name(), subTask.getStartTime(),
                    subTask.getDuration(), subTask.getEpicId());
        });
        assertEquals(epicStatusTestData.epicTaskStatus(), taskManager.getEpic(epicId).getStatus());
    }

    static List<TestHelper.EpicStatusTestData> getEpicStatusTestData() {
        return TestHelper.getEpicStatusTestData();
    }

    @Test
    void hasTimeConfictFalse() {
        taskManager.createTask("Task1", "Description Task1", "NEW",
                LocalDateTime.of(2020, 1, 1, 10, 0, 0),
                Duration.ofMinutes(10));
        assertFalse(taskManager.createTask("Task2", "Description Task2", "NEW",
                LocalDateTime.of(2020, 1, 1, 10, 11, 0),
                Duration.ofMinutes(10)));
    }

    @Test
    void hasTimeConfictTrue() {
        taskManager.createTask("Task1", "Description Task1", "NEW",
                LocalDateTime.of(2020, 1, 1, 10, 0, 0),
                Duration.ofMinutes(10));
        assertTrue(taskManager.createTask("Task2", "Description Task2", "NEW",
                LocalDateTime.of(2020, 1, 1, 10, 8, 0),
                Duration.ofMinutes(10)));
    }

    @Test
    void epicsNotInPrioritizedList() {
        taskManager.createEpic("Epic1", "Description Epic1");
        taskManager.createTask("Task1", "Description Task1", "NEW", LocalDateTime.now(), Duration.ofHours(20));
        taskManager.createSubTask("SubTask1", "Description SubTask1", "NEW", LocalDateTime.now().plusDays(1), Duration.ofHours(20), 1);
        assertFalse(taskManager.getPrioritizedTasks().stream().anyMatch(task -> task.getTaskType() == EPIC), "epics");
        assertTrue(taskManager.getPrioritizedTasks().stream().anyMatch(task -> task.getTaskType() == TASK), "tasks");
        assertTrue(taskManager.getPrioritizedTasks().stream().anyMatch(task -> task.getTaskType() == SUBTASK), "subtasks");
    }

    @Test
    void tasksWithoutStartDateNotInPrioritizedList() {
        taskManager.createTask("Task1", "Description Task1", "NEW", null, Duration.ofHours(20));
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void prioritiziedListOrderedByStartDate() {
        taskManager.createTask("Task1", "Description Task1", "NEW", LocalDateTime.now(), Duration.ofHours(20));
        taskManager.createTask("Task2", "Description Task1", "NEW", LocalDateTime.now().minusDays(100), Duration.ofHours(20));
        taskManager.createTask("Task3", "Description Task1", "NEW", LocalDateTime.now().plusDays(1000), Duration.ofHours(20));
        taskManager.createTask("Task4", "Description Task1", "NEW", LocalDateTime.now(), Duration.ofHours(20));
        taskManager.createTask("Task5", "Description Task1", "NEW", LocalDateTime.now().minusDays(100), Duration.ofHours(20));
        taskManager.createTask("Task6", "Description Task1", "NEW", LocalDateTime.now().minusDays(100), Duration.ofHours(20));
        taskManager.createTask("Task7", "Description Task1", "NEW",
                LocalDateTime.of(2020, 1, 1, 0, 0, 10), Duration.ofHours(20));
        taskManager.createTask("Task8", "Description Task1", "NEW",
                LocalDateTime.of(2020, 1, 1, 0, 0, 10), Duration.ofHours(20));
        LocalDateTime previousStartDate = LocalDateTime.MIN;
        for (Task task : taskManager.getPrioritizedTasks()) {
            assertTrue(previousStartDate.isBefore(task.getStartTime()) || previousStartDate.equals(task.getStartTime()));
            previousStartDate = task.getStartTime();
        }
    }
}
