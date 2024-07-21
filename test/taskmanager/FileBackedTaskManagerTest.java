package taskmanager;

import historymanager.InMemoryHistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static String testFilePath = "resource/test-tasks-storage.csv";

    @BeforeEach
    void init() {
        taskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), new File(testFilePath));
    }

    @Test
    void savedToFileProperly() throws IOException {
        taskManager.createEpic("Epic1", "Description Epic1");
        taskManager.createTask("Task1", "Description Task1", "NEW",
                LocalDateTime.of(2020, 1, 1, 1, 1, 1), Duration.ofHours(20));
        taskManager.createSubTask("SubTask1", "Description SubTask1", "NEW",
                LocalDateTime.of(2020, 1, 1, 1, 1, 1), Duration.ofHours(20), 1);
        taskManager.createTask("Task2", "Description Task2", "NEW",
                LocalDateTime.of(2020, 1, 1, 1, 1, 1), Duration.ofHours(20));
        taskManager.createEpic("Epic2", "Description Epic2");
        taskManager.createSubTask("SubTask2", "Description SubTask2", "NEW",
                LocalDateTime.of(2020, 1, 1, 1, 1, 1), Duration.ofHours(20), 1);
        taskManager.getTask(4);
        taskManager.getEpic(5);
        taskManager.getTask(4);
        taskManager.getTask(2);
        taskManager.getSubTask(6);

        File fileActual = new File(testFilePath);
        File fileExpected = new File("resource/expected_tasks-storage.csv");
        Assertions.assertEquals(-1, filesCompareByByte(fileExpected, fileActual));
    }

    private long filesCompareByByte(File file1, File file2) throws IOException {
        try (BufferedReader fis1 = new BufferedReader(new FileReader(file1));
             BufferedReader fis2 = new BufferedReader(new FileReader(file2))) {

            int ch = 0;
            long pos = 1;
            while ((ch = fis1.read()) != -1) {
                if (ch != fis2.read()) {
                    return pos;
                }
                pos++;
            }
            if (fis2.read() == -1) {
                return -1;
            } else {
                return pos;
            }
        }
    }
}
