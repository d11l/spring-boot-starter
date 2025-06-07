package sa.abdulrahman.starter.validation;

public class Regex {
    public static final String USERNAME = "^(?:[0-9]{9}|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$";
    public static final String OTP = "^\\d{6}$";
    public static final String EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
    public static final String PHONE_NUMBER = "^\\+?[0-9]{9,15}$";
    public static final String PASSWORD = "^[a-zA-Z0-9@_#$]{8,}$";
    public static final String NAME = "^[a-zA-Z\\s]{2,50}$";
}
