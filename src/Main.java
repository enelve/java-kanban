import tasktypes.SubTask;
import tasktypes.Task;

public class Main {

    public static void main(String[] args) {
        KanbanManager kanbanManager = new KanbanManager();

        System.out.println("Создаем две задачи, а также эпик с двумя подзадачами и  эпик с одной подзадачей.");
        kanbanManager.createTask("Task 1", "Task 1 description", "NEW");
        kanbanManager.createTask("Task 2", "Task 2 description", "NEW");
        kanbanManager.createEpic("Epic 1", "Epic 1 description");
        Task epic1 = kanbanManager.findEpicById(3);
        kanbanManager.createSubTask("SubTask 1", "SubTask 1 description","NEW", epic1.getId());
        kanbanManager.createSubTask("SubTask 2", "SubTask 2 description","NEW", epic1.getId());
        kanbanManager.createEpic("Epic 2", "Epic 2 description");
        Task epic2 = kanbanManager.findEpicById(6);
        kanbanManager.createSubTask("SubTask 3", "SubTask 3 description","NEW", epic2.getId());
        System.out.println(kanbanManager);

        System.out.println("Переводим одну из подзадач эпика + задачу в DONE");
        SubTask subTask = kanbanManager.getSubTaskListByEpicId(3).get(0);
        subTask = new SubTask(subTask.getId(), subTask.getName(), subTask.getDescription(),"DONE", epic1.getId());
        kanbanManager.updateSubTask(subTask);
        Task task = kanbanManager.getTaskList().get(0);
        task = new Task(task.getId(),task.getName(), task.getDescription(), "DONE");
        kanbanManager.updateTask(task);
        System.out.println(kanbanManager);

        System.out.println("Удаляем выборочные задачи");
        kanbanManager.removeEpicById(6);
        kanbanManager.removeSubTaskById(4);
        kanbanManager.removeTaskById(1);
        System.out.println(kanbanManager);

        System.out.println("Очищаем все списки задач");
        kanbanManager.clearEpics();
        kanbanManager.clearSubTasks();
        kanbanManager.clearTasks();
        System.out.println(kanbanManager);
    }
}
