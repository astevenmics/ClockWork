package mist.mystralix.Objects;

import java.util.regex.Matcher;

public class TimeHandler {

    public static long parseDuration(String durationInString) {
        long totalMilliseconds = 0;
        try {
            Matcher matcher = java.util.regex.Pattern
                    .compile("(\\d+)\\s*([dhms])")
                    .matcher(durationInString.toLowerCase());

            while (matcher.find()) {
                long durationValue = Long.parseLong(matcher.group(1));
                switch (matcher.group(2)) {
                    case "d":
                        totalMilliseconds += durationValue * 86_400_000L;
                        break;
                    case "h":
                        totalMilliseconds += durationValue * 3_600_000L;
                        break;
                    case "m":
                        totalMilliseconds += durationValue * 60_000L;
                        break;
                    case "s":
                        totalMilliseconds += durationValue * 1_000L;
                        break;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return -1;
        }
        return totalMilliseconds;
    }
}