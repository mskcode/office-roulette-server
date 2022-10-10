package fi.mskcode.officeroulette.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {
    private DateTimeUtil() {}

    public static String formatAsIso8601(Instant instant) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

    public static Instant parseIso8601String(String timestamp) {
        var x = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(timestamp);
        return Instant.from(x);
    }
}
