package mist.mystralix.utils.messages;

public final class ReminderMessages {

    public static final String INVALID_TIME_INPUT =
            "Invalid reminder time provided.\nExample: 1d, 1d20h, 20h15m...";

    public static final String MINIMUM_TIME_INPUT =
            "Time target/duration must be at least 1 minute.";

    public static final String REMINDER_NOT_CREATED_BY_USER =
            "You do not have access to Reminder #%d, as it was created by a different user.";

    private ReminderMessages() {
    }
}
