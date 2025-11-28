package mist.mystralix.Objects;

import java.util.regex.Matcher;

/*
    Solely responsible for converting text into milliseconds
*/
public class TimeHandler {

    /*
        parseDuration
            - Responsible for converting string-defined time into milliseconds
            - Parameter:
                * String durationInString | Contains the user-defined time
                    - Options:
                        * d - days
                        * h - hours
                        * m - minutes
                        * s - seconds
                    - Examples:
                        * 1d20h
                        * 30m25s
    */
    public static long parseDuration(String durationInString) {
        /*
            Initialize totalMilliseconds that will eventually contain the total milliseconds after parsing/converting
        */
        long totalMilliseconds = 0;

        /*
            Try-Catch to ensure that any parsing errors are caught
        */
        try {
            /*
                Finds any matches in the durationInString (in lowercase) based on the regex provided in compile()
            */
            Matcher matcher = java.util.regex.Pattern
                    .compile("(\\d+)\\s*([dhms])")
                    .matcher(durationInString.toLowerCase());

            /*
                - oneSecondInMilliseconds contains 1,000 (1_000)
                - oneMinuteInMilliseconds contains 60,000 (1_000 * 60)
                - oneHourInMilliseconds contains 3,600,000 (1_000 * 60 * 60)
                - oneDayInMilliseconds contains 86,400,000 (1_000 * 24 * 60 * 60)
            */
            long oneSecondInMilliseconds = 1_000;
            long oneMinuteInMilliseconds = 60 * oneSecondInMilliseconds;
            long oneHourInMilliseconds = 60 * oneMinuteInMilliseconds;
            long oneDayInMilliseconds = 24 * oneHourInMilliseconds;

            /*
                Loops through all matches in the durationInString found by the matcher
            */
            while (matcher.find()) {

                /*
                    - durationValue holds the long-converted value found in matcher.group1
                    - matcher.group1 returns the numerical value before the letters
                */
                long durationValue = Long.parseLong(matcher.group(1));

                /*
                    - Switch-Case statement
                    - matcher.group2 returns the letters after the numerical value
                    - Checks whether the letter is d/h/m/s
                */
                switch (matcher.group(2)) {
                    /*
                        - Multiplies oneDayInMilliseconds with durationValue
                        - Adds the product to the totalMilliseconds
                    */
                    case "d" -> totalMilliseconds += durationValue * oneDayInMilliseconds;
                    /*
                        - Multiplies oneHourInMilliseconds with durationValue
                        - Adds the product to the totalMilliseconds
                    */
                    case "h" -> totalMilliseconds += durationValue * oneHourInMilliseconds;
                    /*
                        - Multiplies oneMinuteInMilliseconds with durationValue
                        - Adds the product to the totalMilliseconds
                    */
                    case "m" -> totalMilliseconds += durationValue * oneMinuteInMilliseconds;
                    /*
                        - Multiplies oneSecondInMilliseconds with durationValue
                        - Adds the product to the totalMilliseconds
                    */
                    case "s" -> totalMilliseconds += durationValue * oneSecondInMilliseconds;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            /*
                Returns -1 upon receiving a NumberFormatException error for checks
            */
            return -1;
        }
        return totalMilliseconds;
    }
}