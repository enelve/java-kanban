package taskmanager;

import exception.ManagerSaveException;
import exception.TaskParsingFromStringException;
import historymanager.HistoryManager;
import tasktype.Epic;
import tasktype.SubTask;
import tasktype.Task;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String EMPTY_STRING = "";

    private File file;

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static void main(String[] args) {
        FileBackedTaskManager manager1 = new FileBackedTaskManager(Managers.getDefaultHistory(),
                new File("tasks-storage.csv"));
        manager1.createEpic("Epic1", "Description Epic1");
        manager1.createTask("Task1", "Description Task1", "NEW");
        manager1.createSubTask("SubTask1", "Description SubTask1", "NEW", 1);
        manager1.createTask("Task2", "Description Task2", "NEW");
        manager1.createEpic("Epic2", "Description Epic2");
        manager1.createSubTask("SubTask2", "Description SubTask2", "NEW", 1);
        manager1.getTask(4);
        manager1.getEpic(5);
        manager1.getTask(4);
        manager1.getTask(2);
        manager1.getSubTask(6);
        TaskManager manager2 = FileBackedTaskManager.loadFromFile(new File("tasks-storage.csv"));

        System.out.println(
                compareLists(manager1.getHistory(), manager2.getHistory()) &&
                        compareLists(manager1.getTasks(), manager2.getTasks()) &&
                        compareLists(manager1.getSubTasks(), manager2.getSubTasks()) &&
                        compareLists(manager1.getEpics(), manager2.getEpics())
        );
    }

    private static <T extends Task> boolean compareLists(List<T> expectedList, List<T> actualList) {
        boolean result = true;
        if (expectedList.size() == actualList.size()) {
            for (int i = 0; i < expectedList.size(); i++) {
                result = result &&
                        expectedList.get(i).getId() == actualList.get(i).getId();
            }
        } else {
            result = false;
        }
        return result;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        HashMap<Integer, Task> taskMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // skip first row
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    line = reader.readLine();
                    manager.restoreTaskHistory(taskMap, historyFromString(line));
                } else {
                    Task task = Task.fromString(line);
                    Optional.ofNullable(task).ifPresent(t -> {
                        taskMap.put(t.getId(), task);
                        manager.restoreTask(task);
                    });
                }
            }
        } catch (IOException | TaskParsingFromStringException e) {
            System.out.println("Ошибка запуска приложения: " + e.getMessage());
        }

        manager.file = file;
        return manager;
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> taskHistoryList = manager.getHistory();
        if (!taskHistoryList.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (Task task : taskHistoryList) {
                sb.append(task.getId()).append(",");
            }
            return sb.substring(0, sb.length() - 1);
        }
        return EMPTY_STRING;
    }

    public static List<Integer> historyFromString(String value) {
        if (value != null) {
            return Arrays.stream(value.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    @Override
    public void createEpic(String name, String description) {
        super.createEpic(name, description);
        save();
    }

    @Override
    public void createSubTask(String name, String description, String status, int epicId) {
        super.createSubTask(name, description, status, epicId);
        save();
    }

    @Override
    public void createTask(String name, String description, String status) {
        super.createTask(name, description, status);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task result = super.getTask(id);
        save();

        return result;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask result = super.getSubTask(id);
        save();

        return result;
    }

    @Override
    public Epic getEpic(int id) {
        Epic result = super.getEpic(id);
        save();

        return result;
    }

    private void restoreTask(Task task) {
        switch (task.getTaskType()) {
            case TASK -> tasks.put(task.getId(), task);
            case EPIC -> epics.put(task.getId(), (Epic) task);
            case SUBTASK -> {
                subTasks.put(task.getId(), (SubTask) task);
                Epic parent = epics.get(((SubTask) task).getEpicId());
                parent.linkSubTaskToEpic(task.getId());
            }
        }
    }

    private void restoreTaskHistory(Map<Integer, Task> taskMap, List<Integer> idList) {
        for (Integer id : idList) {
            getHistoryManager().add(taskMap.get(id));
        }
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            String header = "id,type,name,status,description,start time,duration,end time, epic,";
            String nextLine = "\n";
            fileWriter.write(header + nextLine);
            List<Task> taskList = new LinkedList<>();
            taskList.addAll(super.getTasks());
            taskList.addAll(super.getEpics());
            taskList.addAll(super.getSubTasks());
            for (Task task : taskList) {
                fileWriter.write(task.toString() + nextLine);
            }
            fileWriter.write(nextLine);
            fileWriter.write(historyToString(this.getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка ввода вывода при сохранении в файл");
        }
    }
}
