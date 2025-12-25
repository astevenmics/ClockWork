package mist.mystralix.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeHandler {

    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)\\s*([dhms])");

    private static final long SECOND = 1_000;

    private static final long MINUTE = 60 * SECOND;

    private static final long HOUR = 60 * MINUTE;

    private static final long DAY = 24 * HOUR;

    public static long parseDuration(String inputDuration) {
        long totalMilliseconds = 0;

        try {
            Matcher matcher = DURATION_PATTERN.matcher(inputDuration.toLowerCase());

            while (matcher.find()) {

                long value = Long.parseLong(matcher.group(1));

                String unit = matcher.group(2);

                switch (unit) {
                    case "d" -> totalMilliseconds += value * DAY;
                    case "h" -> totalMilliseconds += value * HOUR;
                    case "m" -> totalMilliseconds += value * MINUTE;
                    case "s" -> totalMilliseconds += value * SECOND;
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Failed to parse duration: " + e.getMessage());
            return -1;
        }

        return totalMilliseconds;
    }

    private TimeHandler() {
    }

}