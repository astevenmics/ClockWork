package mist.mystralix.domain.enums;

import java.awt.*;
import java.util.Arrays;

public enum TaskStatus {

    ALL(
            "♾️",
            "All",
            1,
            Color.decode("#F1EDFD")
    ),

    COMPLETED(
            "✅",
            "Completed",
            2,
            Color.decode("#6FE16B")
    ),

    INPROGRESS(
            "⏳",
            "In Progress",
            3,
            Color.decode("#FF8000")
    ),

    ARCHIVED(
            "\uD83D\uDCDA",
            "Archived",
            4,
            Color.decode("#744F34")
    ),

    CANCELLED(
            "\uD83D\uDEAB",
            "Cancelled",
            5,
            Color.decode("#FF0000")
    );

    private final String ICON;

    private final String STRING_VALUE;

    private final int INT_VALUE;

    private final Color COLOR_VALUE;

    TaskStatus(String icon, String stringValue, int intValue, Color colorValue) {
        this.ICON = icon;
        this.STRING_VALUE = stringValue;
        this.INT_VALUE = intValue;
        this.COLOR_VALUE = colorValue;
    }

    public String getIcon() {
        return ICON;
    }

    public String getStringValue() {
        return STRING_VALUE;
    }

    public int getIntValue() {
        return INT_VALUE;
    }

    public Color getColorValue() {
        return COLOR_VALUE;
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