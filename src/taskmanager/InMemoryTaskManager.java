package taskmanager;

import historymanager.HistoryManager;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskDate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static task.Task.TaskStatus.*;
import static task.Task.TaskType.TASK;

public class InMemoryTaskManager implements TaskManager {
    protected int id;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HashMap<Integer, Task> tasks;
    private final HistoryManager historyManager;

    protected InMemoryTaskManager(HistoryManager historyManager) {
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
    public boolean createSubTask(String name, String description, String status, LocalDateTime startTime,
                                 Duration duration, int epicId) {
        SubTask subTask = null;
        if (epics.containsKey(epicId)) {
            int nextId = generateId();
            subTask = new SubTask(nextId, name, description, status, startTime, duration, epicId);
            subTasks.put(nextId, subTask);
            Epic parentEpic = epics.get(subTask.getEpicId());
            parentEpic.linkSubTaskToEpic(subTask.getId());
            updateParentEpic(parentEpic.getId());
        }
        return hasTimeConfict(subTask);
    }

    @Override
    public boolean createTask(String name, String description, String status, LocalDateTime startTime, Duration duration) {
        int nextId = generateId();
        Task task = new Task(nextId, name, description, status, TASK, startTime, duration);
        tasks.put(task.getId(), task);
        return hasTimeConfict(task);
    }

    @Override
    public boolean updateTask(Task task) {
        tasks.put(task.getId(), task);
        return hasTimeConfict(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic newVersion = null;
        if (epics.containsKey(epic.getId())) {
            Epic oldVersion = getEpic(epic.getId());
            newVersion = new Epic(oldVersion.getId(), epic.getName(), epic.getDescription(),
                    oldVersion.getStatus().name(), oldVersion.getSubTasksId(), oldVersion.getStartTime(),
                    oldVersion.getDuration(), oldVersion.getEndTime());
            epics.put(newVersion.getId(), newVersion);
        }
    }

    @Override
    public boolean updateSubTask(SubTask subTask) {
        SubTask newVersion = null;
        if (epics.containsKey(subTask.getEpicId()) && subTasks.containsKey(subTask.getId())) {
            SubTask oldVersion = getSubTask(subTask.getId());
            newVersion = new SubTask(oldVersion.getId(), subTask.getName(), subTask.getDescription(),
                    subTask.getStatus().name(), subTask.getStartTime(), subTask.getDuration(), subTask.getEpicId());
            subTasks.put(newVersion.getId(), newVersion);
            updateParentEpic(newVersion.getEpicId());
        }
        return hasTimeConfict(newVersion);
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
        updateParentEpic(epic.getId());
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
    public List<Task> getAll() {
        List<Task> result = new ArrayList<>();
        result.addAll(getTasks());
        result.addAll(getEpics());
        result.addAll(getSubTasks());
        return result;
    }

    @Override
    public List<SubTask> getEpicSubtasks(int id) {
        return epics.get(id).getSubTasksId()
                .stream().map(subTasks::get)
                .toList();
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        Set<Task> result = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        result.addAll(getTasks().stream().filter(task -> task.getStartTime() != null).toList());
        result.addAll(getSubTasks().stream().filter(task -> task.getStartTime() != null).toList());
        return result;
    }

    private int generateId() {
        return ++id;
    }

    private void updateParentEpic(int id) {
        List<SubTask> epicSubTasks = getEpicSubtasks(id);
        Task.TaskStatus epicStatus = calculateEpicStatus(epicSubTasks);
        TaskDate taskDate = calculateEpicDates(epicSubTasks);
        Epic epic = epics.get(id);
        epic = new Epic(epic.getId(), epic.getName(), epic.getDescription(), epicStatus.name(),
                epic.getSubTasksId(), taskDate.startTime(), taskDate.duration(), taskDate.endTime());
        epics.put(epic.getId(), epic);
    }

    private Task.TaskStatus calculateEpicStatus(List<SubTask> epicSubTasks) {
        if (epicSubTasks == null) {
            return NEW;
        }
        List<Task> inProgressSubTasks = new ArrayList<>();
        List<Task> doneSubTasks = new ArrayList<>();
        epicSubTasks.forEach(subTask -> {
            if (IN_PROGRESS == subTask.getStatus()) {
                inProgressSubTasks.add(subTask);
            } else if (DONE == subTask.getStatus()) {
                doneSubTasks.add(subTask);
            }
        });
        if (epicSubTasks.size() == doneSubTasks.size()) {
            return DONE;
        } else if (doneSubTasks.isEmpty() && inProgressSubTasks.isEmpty()) {
            return NEW;
        } else {
            return IN_PROGRESS;
        }
    }

    private TaskDate calculateEpicDates(List<SubTask> epicSubTasks) {
        LocalDateTime startDate = epicSubTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
        LocalDateTime endDate = epicSubTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
        Duration duration = null;
        if (startDate != null && endDate != null) {
            duration = Duration.between(startDate, endDate);
        }
        return new TaskDate(startDate, endDate, duration);
    }

    private boolean hasTimeConfict(Task task) {
        if (task != null && task.getEndTime() != null && task.getStartTime() != null) {
            return getPrioritizedTasks().stream()
                    .filter(prioritizedTask -> prioritizedTask.getId() != task.getId())
                    .anyMatch(prioritizedTask -> task.getStartTime().isBefore(prioritizedTask.getEndTime())
                            && task.getEndTime().isAfter(prioritizedTask.getStartTime()));
        } else {
            return false;
        }
    }
}

