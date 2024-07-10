package taskmanager;

import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagersTest {

    @RepeatedTest(10)
     void defaultManagersInitializedProperly() {
        assertTrue(Managers.getDefault() instanceof InMemoryTaskManager);
    }
}

