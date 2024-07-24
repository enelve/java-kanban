import exception.TaskParsingFromStringException;
import org.junit.jupiter.api.Test;
import task.Task;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskTest {

    @Test
    public void TaskParsingFromStringException() {
        assertThrows(TaskParsingFromStringException.class, () -> {
            Task.fromString("file1");
        }, "Ожидаемое исключение при ошибке парсинга");
    }
}
