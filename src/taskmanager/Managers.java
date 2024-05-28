package taskmanager;

import historymanager.HistoryManager;
import historymanager.InMemoryHistoryManager;

public final class Managers {
    private Managers() {
    }
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
