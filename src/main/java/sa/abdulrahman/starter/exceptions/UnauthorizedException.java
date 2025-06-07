package sa.abdulrahman.starter.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Username or password is incorrect");
    }
}
