package historymanager;

import tasktype.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    final List<Task> taskHistoryList = new ArrayList<>();
    private static final int TASK_HISTORY_DEPTH = 10;

    @Override
    public void add(Task task) {
        if (taskHistoryList.size() == TASK_HISTORY_DEPTH) {
            taskHistoryList.remove(0);
        }
        taskHistoryList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return taskHistoryList;
    }
}
