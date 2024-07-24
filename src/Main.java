import taskmanager.Managers;
import taskmanager.TaskManager;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        manager.createEpic("Epic1", "Description");
        manager.createTask("Task1", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(20));
        manager.createSubTask("SubTask1", "Description", "NEW", LocalDateTime.now().minusDays(10), Duration.ofHours(20), 1);
        manager.createTask("Task2", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(20));
        manager.createEpic("Epic2", "Description");
        manager.createSubTask("SubTask2", "Description", "NEW", LocalDateTime.now(), Duration.ofHours(100), 1);

        manager.getTask(4);
        manager.getEpic(5);
        manager.getTask(4);
        manager.getTask(2);
        manager.getSubTask(6);
        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
