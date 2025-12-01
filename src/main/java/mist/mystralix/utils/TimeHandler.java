package mist.mystralix.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class responsible for converting human-readable duration strings
 * (e.g., "1d20h", "30m25s") into milliseconds.
 *
 * <p>Supported time units:
 * <ul>
 *     <li><strong>d</strong> - days</li>
 *     <li><strong>h</strong> - hours</li>
 *     <li><strong>m</strong> - minutes</li>
 *     <li><strong>s</strong> - seconds</li>
 * </ul>
 *
 * <p>Examples:</p>
 * <pre>
 *  "1d20h"   → 1 day + 20 hours
 *  "30m25s"  → 30 minutes + 25 seconds
 * </pre>
 */
public class TimeHandler {

    /** Regex pattern for matching numeric value + time unit (e.g., "20h", "15 m") */
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)\\s*([dhms])");

    /** Constant representing 1 second in milliseconds. */
    private static final long SECOND = 1_000;

    /** Constant representing 1 minute in milliseconds. */
    private static final long MINUTE = 60 * SECOND;

    /** Constant representing 1 hour in milliseconds. */
    private static final long HOUR = 60 * MINUTE;

    /** Constant representing 1 day in milliseconds. */
    private static final long DAY = 24 * HOUR;

    /**
     * Converts a duration string (e.g. "1d20h") into the total number of milliseconds.
     *
     * @param inputDuration the user-defined duration string to parse
     * @return total duration in milliseconds, or {@code -1} if parsing fails
     *
     * <p>Example:</p>
     * <pre>
     *  parseDuration("1h30m")   → 5,400,000
     *  parseDuration("2d")      → 172,800,000
     * </pre>
     */
    public static long parseDuration(String inputDuration) {
        long totalMilliseconds = 0;

        try {
            // Create a matcher that finds segments like "10h", "25m", etc.
            Matcher matcher = DURATION_PATTERN.matcher(inputDuration.toLowerCase());

            // Iterate through every found segment in the string
            while (matcher.find()) {

                // Numeric part (e.g., "10" from "10h")
                long value = Long.parseLong(matcher.group(1));

                // Time unit part (e.g., "h" from "10h")
                String unit = matcher.group(2);

                // Convert each segment to milliseconds and accumulate the result
                switch (unit) {
                    case "d" -> totalMilliseconds += value * DAY;
                    case "h" -> totalMilliseconds += value * HOUR;
                    case "m" -> totalMilliseconds += value * MINUTE;
                    case "s" -> totalMilliseconds += value * SECOND;
                }
            }

        } catch (NumberFormatException e) {
            // Log the parsing issue and return -1 to indicate invalid input
            System.out.println("Failed to parse duration: " + e.getMessage());
            return -1;
        }

        return totalMilliseconds;
    }
}
