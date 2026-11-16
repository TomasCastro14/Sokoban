package exception;

/** A level could not be found or read. It is not a format error: that is InvalidLevelException. */
public class LevelNotFoundException extends Exception {

    public LevelNotFoundException(String message) {
        super(message);
    }

    public LevelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
