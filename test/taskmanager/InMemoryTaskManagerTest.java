package taskmanager;

import historymanager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void init() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

}
