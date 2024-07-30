package taskmanager;

import historymanager.InMemoryHistoryManager;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static final String testFilePath = "resource/test-tasks-storage.csv";

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
        assertTrue(compare(fileExpected, fileActual));
    }

    private boolean compare(File file1, File file2) throws IOException {
        boolean result;
        BufferedReader reader1 = new BufferedReader(new FileReader(file1));
        BufferedReader reader2 = new BufferedReader(new FileReader(file2));
        String line1 = reader1.readLine();
        String line2 = reader2.readLine();
        int lineNum = 1;
        boolean areEqual = true;
        while (line1 != null || line2 != null) {
            if (line1 == null || line2 == null) {
                areEqual = false;
                break;
            } else if (!line1.equalsIgnoreCase(line2)) {
                areEqual = false;
                break;
            }
            line1 = reader1.readLine();
            line2 = reader2.readLine();
            lineNum++;
        }
        if (areEqual) {
            result = true;
            System.out.println("Both the files have same content");
        } else {
            result = false;
            System.out.println("Both the files have different content");
            System.out.println("In both files, there is a difference at line number: " + lineNum);
            System.out.println("One file has " + line1 + " and another file has " + line2 + " at line " + lineNum);
        }
        reader1.close();
        reader2.close();
        return result;
    }
}
