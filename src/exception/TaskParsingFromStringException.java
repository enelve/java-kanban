package exception;

public class TaskParsingFromStringException extends RuntimeException {
    public TaskParsingFromStringException(String message) {
        super(message);
    }
}
