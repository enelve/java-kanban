package historymanager;

import tasktype.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node<Task>> taskHistoryMap = new HashMap<>();
    private final TaskHistoryList taskHistoryList = new TaskHistoryList();

    private static class TaskHistoryList {
        private Node<Task> first;
        private Node<Task> last;
        private int size = 0;

        private Node<Task> linkLast(Task task) {
            final Node<Task> l = last;
            final Node<Task> newNode = new Node<>(l, task, null);
            last = newNode;
            if (l == null)
                first = newNode;
            else
                l.next = newNode;
            size++;

            return newNode;
        }

        private List<Task> getTasks() {
            List<Task> result = new ArrayList<>();
            if (size != 0) {
                for (Node<Task> t = first; t != null; t = t.next)
                    result.add(t.data);
            }
            return result;
        }

        private void removeNode(Node<Task> node) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;

            if (prev == null) {
                first = next;
            } else {
                prev.next = next;
                node.prev = null;
            }
            if (next == null) {
                last = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            node.data = null;
            size--;
        }
    }

    @Override
    public void add(Task task) {
        int taskId = task.getId();
        if (taskHistoryMap.containsKey(taskId)) {
            taskHistoryList.removeNode(taskHistoryMap.get(taskId));
        }
        Node<Task> taskNode = taskHistoryList.linkLast(task);
        taskHistoryMap.put(taskId, taskNode);
    }

    @Override
    public List<Task> getHistory() {
        return taskHistoryList.getTasks();
    }

    @Override
    public void remove(int id) {
        taskHistoryList.removeNode(taskHistoryMap.get(id));
        taskHistoryMap.remove(id);
    }
}
