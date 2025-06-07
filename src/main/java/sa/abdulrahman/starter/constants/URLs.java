package sa.abdulrahman.starter.constants;

public class URLs {
    public static class AUTH {
        public static final String BASE_URL ="/api/v1/auth";
        public static final String BASE_URL_PUBLIC ="/api/v1/auth/**";
        public static final String LOGIN ="/login";
        public static final String LOGIN_OTP ="/login-otp";
        public static final String REQUEST_OTP ="/request-otp";
        public static final String REGISTER ="/register";
        public static final String REFRESH_TOKEN ="/refresh-token";
        public static final String FORGOT_PASSWORD ="/forgot-password";
        public static final String RESET_PASSWORD_WITH_OTP ="/reset-password";
    }

    public static class USER {
        public static final String BASE_URL ="/api/v1/user";
        public static final String RESET_PASSWORD ="/reset-password";
        public static final String UPDATE_PROFILE ="/update-profile";
    }

}
