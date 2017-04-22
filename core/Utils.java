package core;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Utility relative
 * Kai
 */
class Utils {
    /**
     * @param s   The String to be repeated
     * @param num The number of time to repeat String s.
     * @return String A String object, which is the String s appended to itself num times.
     */
    static String line(String s, int num) {
        String a = "";
        for (int i = 0; i < num; i++) {
            a += s;
        }
        return a;
    }

    static void print(String content) {
        System.out.println(content);
    }

    static String timestampToDate(long timeStamp) {
        /* Define the time zone for Dallas CST */
        ZoneId zoneId = ZoneId.of ( "America/Chicago" );

        Instant i = Instant.ofEpochSecond ( timeStamp );
        ZonedDateTime zdt = ZonedDateTime.ofInstant ( i, zoneId );

        /* ZonedDateTime toLocalDate() method will display in a simple format */
        return zdt.toLocalDate().toString();
    }

    static String timestampToDateTime(long timeStamp) {
        /* Define the time zone for Dallas CST */
        ZoneId zoneId = ZoneId.of ( "America/Chicago" );

        Instant i = Instant.ofEpochSecond ( timeStamp );
        ZonedDateTime zdt = ZonedDateTime.ofInstant ( i, zoneId );

        /* ZonedDateTime toLocalDate() method will display in a simple format */
        return zdt.toLocalDateTime().toString();
    }

    static long dateTimeToTimestamp(String dateTime) {
        /* Define the time zone for Dallas CST */
        ZoneId zoneId = ZoneId.of ( "America/Chicago" );

        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.from(f.parse(dateTime));

        ZonedDateTime zdt = ZonedDateTime.of(ldt, zoneId);
        return zdt.toInstant().toEpochMilli() / 1000;
    }

    static long dateToTimeStamp(String date) {
        /* Define the time zone for Dallas CST */
        ZoneId zoneId = ZoneId.of ( "America/Chicago" );

        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.from(f.parse(date));

        ZonedDateTime zdt = ZonedDateTime.of(ld.atTime(0, 0, 0), zoneId);
        return zdt.toInstant().toEpochMilli() / 1000;
    }
}
