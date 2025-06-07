package sa.abdulrahman.starter.exceptions;

public class InvalidOTPException extends RuntimeException {

    public InvalidOTPException() {
        super("OTP is not correct or expired");
    }
}