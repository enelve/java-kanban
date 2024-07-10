package taskmanager;

import historymanager.HistoryManager;
import tasktype.Epic;
import tasktype.SubTask;
import tasktype.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static tasktype.Task.TaskStatus.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, Task> tasks;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.tasks = new HashMap<>();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void createEpic(String name, String description) {
        int nextId = generateId();
        Epic epic = new Epic(nextId, name, description);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(String name, String description, String status, int epicId) {
        if (epics.containsKey(epicId)) {
            int nextId = generateId();
            SubTask subTask = new SubTask(nextId, name, description, status, epicId);
            subTasks.put(nextId, subTask);
            Epic parentEpic = getEpic(subTask.getEpicId());
            parentEpic.linkSubTaskToEpic(subTask.getId());
            updateEpicStatus(parentEpic.getId());
        }
    }

    @Override
    public void createTask(String name, String description, String status) {
        int nextId = generateId();
        Task task = new Task(nextId, name, description, status);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldVersion = getEpic(epic.getId());
            Epic newVersion = new Epic(oldVersion.getId(), epic.getName(), epic.getDescription(), oldVersion.getStatus().name(),
                    oldVersion.getSubTasksId());
            epics.put(newVersion.getId(), newVersion);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId()) && subTasks.containsKey(subTask.getId())) {
            SubTask oldVersion = getSubTask(subTask.getId());
            SubTask newVersion = new SubTask(oldVersion.getId(), subTask.getName(), subTask.getDescription(),
                    subTask.getStatus().name(), oldVersion.getEpicId());
            subTasks.put(newVersion.getId(), newVersion);
            updateEpicStatus(newVersion.getEpicId());
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask task = subTasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic task = epics.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        Epic epic = getEpic(getSubTask(id).getEpicId());
        epic.unlinkSubTaskFromEpic(id);
        updateEpicStatus(epic.getId());
        subTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        for (int subTaskId : epics.get(id).getSubTasksId()) {
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getEpicSubtasks(int id) {
        ArrayList<SubTask> subTasksByEpic = new ArrayList<>();
        for (int subTaskId : epics.get(id).getSubTasksId()) {
            SubTask subTask = getSubTask(subTaskId);
            subTasksByEpic.add(subTask);
        }
        return subTasksByEpic;
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            removeSubTask(subTask.getId());
        }
    }

    @Override
    public void clearEpics() {
        clearSubTasks();
        epics.clear();
    }

    private int generateId() {
        return ++id;
    }

    private void updateEpicStatus(int id) {
        List<SubTask> epicSubTusks = getEpicSubtasks(id);
        if (!epicSubTusks.isEmpty()) {
            List<Task> inProgressSubTasks = new ArrayList<>();
            List<Task> doneSubTasks = new ArrayList<>();
            for (Task subTask : epicSubTusks) {
                if (IN_PROGRESS == subTask.getStatus()) {
                    inProgressSubTasks.add(subTask);
                } else if (DONE == subTask.getStatus()) {
                    doneSubTasks.add(subTask);
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
            Epic epic = getEpic(id);
            epic = new Epic(epic.getId(),
                    epic.getName(),
                    epic.getDescription(),
                    epicStatus.name(),
                    epic.getSubTasksId());
            epics.put(epic.getId(), epic);
        }
    }
}
