package sa.abdulrahman.starter.exceptions;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Invalid token");
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}