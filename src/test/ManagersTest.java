package test;

import org.junit.jupiter.api.RepeatedTest;

import org.junit.jupiter.api.Test;
import taskmanager.InMemoryTaskManager;
import taskmanager.Managers;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManagersTest {

    @RepeatedTest(10)
     void defaultManagersInitializedProperly() {
        assertTrue(Managers.getDefault() instanceof InMemoryTaskManager);
    }
}

