import tasktypes.Epic;
import tasktypes.SubTask;
import tasktypes.Task;

public class Main {

    public static void main(String[] args) {
        KanbanManager kanbanManager = new KanbanManager();

        System.out.println("Создаем две задачи, а также эпик с двумя подзадачами и  эпик с одной подзадачей.");
        kanbanManager.addTask(new Task(kanbanManager.generateId(), "model.Task 1", "model.Task 1 desciption",
                Task.TaskStatus.valueOf("NEW")));
        kanbanManager.addTask(new Task(kanbanManager.generateId(), "model.Task 2", "model.Task 2 desciption",
                Task.TaskStatus.valueOf("NEW")));
        Epic epic = new Epic(kanbanManager.generateId(), "model.Epic 1", "model.Epic 1 description");
        kanbanManager.addEpic(epic);
        kanbanManager.addSubTask(new SubTask(kanbanManager.generateId(), "model.SubTask 1", "model.SubTask 1 desciption",
                Task.TaskStatus.valueOf("NEW"), epic.getId()));
        kanbanManager.addSubTask(new SubTask(kanbanManager.generateId(), "model.SubTask 02", "model.SubTask 2 desciption",
                Task.TaskStatus.valueOf("NEW"), epic.getId()));

        Epic epic2 = new Epic(kanbanManager.generateId(), "model.Epic 2", "model.Epic 2 description");
        SubTask subTask3 = new SubTask(kanbanManager.generateId(), "model.SubTask 3", "model.SubTask 3 desciption",
                Task.TaskStatus.valueOf("NEW"), epic2.getId());
        kanbanManager.addEpic(epic2);
        kanbanManager.addSubTask(subTask3);
        System.out.println(kanbanManager);

        System.out.println("Переводим одну из подзадач эпика + задачу в DONE");
        SubTask subTaskToBeDone = kanbanManager.getSubTaskListByEpicId(2).get(0);
        subTaskToBeDone = new SubTask(subTaskToBeDone.getId(), subTaskToBeDone.getName(), subTaskToBeDone.getDescription(),
                Task.TaskStatus.valueOf("DONE"), epic.getId());
        kanbanManager.updateSubTask(subTaskToBeDone);
        Task taskToBeDone = kanbanManager.getTaskList().get(0);
        taskToBeDone = new Task(taskToBeDone.getId(), taskToBeDone.getName(), taskToBeDone.getDescription(),
                Task.TaskStatus.valueOf("DONE"));
        kanbanManager.updateTask(taskToBeDone);
        System.out.println(kanbanManager);

        System.out.println("Удаляем выборочные задачи");
        kanbanManager.removeEpicById(kanbanManager.getEpicList().get(0).getId());
        kanbanManager.removeSubTaskById(kanbanManager.getSubTaskList().get(0).getId());
        kanbanManager.removeTaskById(kanbanManager.getTaskList().get(0).getId());
        System.out.println(kanbanManager);

        System.out.println("Очищаем все списки задач");
        kanbanManager.clearEpics();
        kanbanManager.clearSubTasks();
        kanbanManager.clearTasks();
        System.out.println(kanbanManager);
    }
}
