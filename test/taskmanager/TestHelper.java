package taskmanager;

import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class TestHelper {

    record EpicStatusTestData(List<SubTask> subTaskList, Task.TaskStatus epicTaskStatus) {
    }

    static List<EpicStatusTestData> getEpicStatusTestData() {
        return List.of(
                new EpicStatusTestData(
                        List.of(
                                new SubTask(1, "SubTask2", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(100), 1),
                                new SubTask(2, "SubTask2", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(100), 1)
                        ), Task.TaskStatus.NEW),
                new EpicStatusTestData(
                        List.of(
                                new SubTask(1, "SubTask2", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(100), 1),
                                new SubTask(2, "SubTask2", "Description", "IN_PROGRESS", LocalDateTime.now(), Duration.ofHours(100), 1)
                        ), Task.TaskStatus.IN_PROGRESS),
                new EpicStatusTestData(
                        List.of(
                                new SubTask(1, "SubTask2", "Description", "DONE", LocalDateTime.now(), Duration.ofHours(100), 1),
                                new SubTask(2, "SubTask2", "Description", "DONE", LocalDateTime.now(), Duration.ofHours(100), 1)
                        ), Task.TaskStatus.DONE),
                new EpicStatusTestData(
                        List.of(
                                new SubTask(1, "SubTask2", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(100), 1),
                                new SubTask(2, "SubTask2", "Description", "DONE", LocalDateTime.now(), Duration.ofHours(100), 1)
                        ), Task.TaskStatus.IN_PROGRESS),
                new EpicStatusTestData(
                        List.of(
                                new SubTask(1, "SubTask2", "Description", "IN_PROGRESS", LocalDateTime.now(), Duration.ofHours(100), 1),
                                new SubTask(2, "SubTask2", "Description", "IN_PROGRESS", LocalDateTime.now(), Duration.ofHours(100), 1)
                        ), Task.TaskStatus.IN_PROGRESS),
                new EpicStatusTestData(
                        List.of(),
                        Task.TaskStatus.NEW)
        );
    }
}
