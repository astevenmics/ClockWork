package mist.mystralix.Enums;

import java.util.Arrays;

public enum TaskStatus {

    ALL("All", 1),
    COMPLETED("Completed", 2),
    INPROGRESS("In Progress", 3),
    ARCHIVED("Archived", 4),
    CANCELLED("Cancelled", 5);

    private final String STRING_VALUE;
    private final int INT_VALUE;

    TaskStatus(String stringValue, int intValue) {
        this.STRING_VALUE = stringValue;
        this.INT_VALUE = intValue;
    }

    public String getValue() {
        return STRING_VALUE;
    }

    public int getIntValue() {
        return INT_VALUE;
    }

    public static TaskStatus getTaskStatus(String stringValue) {
        return Arrays.stream(TaskStatus.values())
                .filter(taskStatus -> taskStatus.STRING_VALUE.equals(stringValue))
                .findFirst()
                .orElse(null);
    }

    public static TaskStatus getTaskStatus(int intValue) {
        return Arrays.stream(TaskStatus.values())
                .filter(taskStatus -> taskStatus.INT_VALUE == intValue)
                .findFirst()
                .orElse(null);
    }

}