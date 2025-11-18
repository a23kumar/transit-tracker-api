package com.transittracker.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations
 */
public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    /**
     * Formats a Unix timestamp (seconds since epoch) into a readable date/time string
     * 
     * @param unixTimestamp Unix timestamp in seconds
     * @return Formatted date/time string or "N/A" if timestamp is 0
     */
    public static String formatTimestamp(long unixTimestamp) {
        if (unixTimestamp == 0) {
            return "N/A";
        }
        Instant instant = Instant.ofEpochSecond(unixTimestamp);
        return FORMATTER.format(instant);
    }
}

