package sa.abdulrahman.starter.exceptions;

public class UsernameAlreadyTakenException extends RuntimeException {

    public UsernameAlreadyTakenException() {
        super("Username is already taken");
    }

    public UsernameAlreadyTakenException(String message) {
        super(message);
    }

    public UsernameAlreadyTakenException(String message, Throwable cause) {
        super(message, cause);
    }
}
