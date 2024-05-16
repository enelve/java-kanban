import tasktypes.Epic;
import tasktypes.SubTask;
import tasktypes.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static tasktypes.Task.TaskStatus.*;

public class KanbanManager {
    private int id;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, Task> tasks;

    public KanbanManager() {
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.tasks = new HashMap<>();
    }

    public List<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    public List<SubTask> getSubTaskList() {
        return new ArrayList<>(subTasks.values());
    }

    public List<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    public int generateId() {
        return id++;
    }
    public void addEpic(Epic epic) {
        Epic newEpic = new Epic(epic.getId(),
                epic.getName(),
                epic.getDescription()
        );
        epics.put(newEpic.getId(), newEpic);
    }
    public void addSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            subTasks.put(subTask.getId(), subTask);
            Epic parentEpic = findEpicById(subTask.getEpicId());
            parentEpic.linkSubTaskToEpic(subTask.getId());
            updateEpicStatus(parentEpic.getId());
        }
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldVersion = findEpicById(epic.getId());
            Epic newVersion = new Epic(oldVersion.getId(),
                    epic.getName(),
                    epic.getDescription(),
                    oldVersion.getStatus(),
                    oldVersion.getSubTasksId());
            epics.put(newVersion.getId(), newVersion);
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId()) && subTasks.containsKey(subTask.getId())) {
            SubTask oldSubTask = findSubTaskById(subTask.getId());
            SubTask newSubTask = new SubTask(oldSubTask.getId(),
                    subTask.getName(),
                    subTask.getDescription(),
                    subTask.getStatus(),
                    oldSubTask.getEpicId());
            subTasks.put(newSubTask.getId(), newSubTask);
            updateEpicStatus(newSubTask.getEpicId());
        }
    }

    public List<SubTask> getSubTaskListByEpicId(int id) {
        ArrayList<SubTask> subTasksByEpic = new ArrayList<>();
        for (int subTaskId : epics.get(id).getSubTasksId()) {
            SubTask subTask = findSubTaskById(subTaskId);
            subTasksByEpic.add(subTask);
        }
        return subTasksByEpic;
    }

    public Task findTaskById(int id) {
        return tasks.get(id);
    }

    public SubTask findSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Epic findEpicById(int id) {
        return epics.get(id);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeSubTaskById(int id) {
        Epic epic = findEpicById(findSubTaskById(id).getEpicId());
        epic.unlinkSubTaskFromEpic(id);
        updateEpicStatus(epic.getId());
        subTasks.remove(id);
    }

    public void removeEpicById(int id) {
        for (int subTaskId : epics.get(id).getSubTasksId()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            removeSubTaskById(subTask.getId());
        }

    }

    public void clearEpics() {
        clearSubTasks();
        epics.clear();
    }

    private void updateEpicStatus(int id) {
       List<SubTask> epicSubTusks = getSubTaskListByEpicId(id);
        List<Task> newSubTasks = new ArrayList<>();
        List<Task> inProgressSubTasks = new ArrayList<>();
        List<Task> doneSubTasks = new ArrayList<>();
        for (Task subTask : epicSubTusks) {
            switch (subTask.getStatus()) {
                case NEW -> newSubTasks.add(subTask);
                case IN_PROGRESS -> inProgressSubTasks.add(subTask);
                case DONE -> doneSubTasks.add(subTask);
            }
        }
        Task.TaskStatus epicStatus;
        if (epicSubTusks.size() == doneSubTasks.size()) {
            epicStatus = DONE;
        } else if (doneSubTasks.isEmpty() && inProgressSubTasks.isEmpty()) {
            epicStatus = NEW;
        } else {
            epicStatus = IN_PROGRESS;
        }
        Epic epic = findEpicById(id);
        epic = new Epic(epic.getId(),
                epic.getName(),
                epic.getDescription(),
                epicStatus,
                epic.getSubTasksId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Task list:\n");
        for (Task task : tasks.values()) {
            result.append(task.toString()).append("\n");
        }
        result.append("Epic list:\n");
        for (Epic epic : epics.values()) {
            result.append(epic.toString()).append("\n");
            if (!epic.getSubTasksId().isEmpty()) {
                for (int i : epic.getSubTasksId()) {
                    result.append("    ").append(subTasks.get(i).toString()).append("\n");
                }
            }
        }
        return result.toString();
    }
}
