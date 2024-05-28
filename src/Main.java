import taskmanager.Managers;
import taskmanager.TaskManager;
import tasktype.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        manager.createEpic("Epic1", "Description");
        manager.createTask("Task1", "Description", "NEW");
        manager.createSubTask("SubTask1", "Description", "NEW", 1);
        manager.getTask(2);
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
