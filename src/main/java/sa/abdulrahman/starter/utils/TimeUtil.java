package sa.abdulrahman.starter.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtil {
    public static LocalDateTime getCurrentTime() {
        return ZonedDateTime.now(ZoneId.of("Asia/Riyadh")).toLocalDateTime();
    }
}
