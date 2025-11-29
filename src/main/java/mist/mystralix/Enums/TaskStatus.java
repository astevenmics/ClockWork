package mist.mystralix.Enums;

import java.awt.*;
import java.util.Arrays;

/**
 * Represents all possible lifecycle states a user task may have.
 *
 * <p>Each status contains four properties used across the application:</p>
 * <ul>
 *     <li><b>Icon</b> – Emoji or symbol to visually represent the status</li>
 *     <li><b>String label</b> – Human-readable name used in UIs</li>
 *     <li><b>Integer code</b> – Numerical ID used in slash command options</li>
 *     <li><b>Color</b> – Embed color associated with the status</li>
 * </ul>
 *
 * <p>This enum is also used to automatically populate Discord option choices
 * for commands such as <code>/task list</code> or <code>/task update</code>.
 * This makes UI generation consistent and avoids duplication.</p>
 */
public enum TaskStatus {

    /** Represents all statuses; used for filtering or listing operations. */
    ALL(
            "♾️",
            "All",
            1,
            Color.decode("#F1EDFD")
    ),

    /** Indicates a successfully completed task. */
    COMPLETED(
            "✅",
            "Completed",
            2,
            Color.decode("#6FE16B")
    ),

    /** Indicates a task currently in progress. */
    INPROGRESS(
            "⏳",
            "In Progress",
            3,
            Color.decode("#FF8000")
    ),

    /** Indicates a task that has been archived (not active but saved). */
    ARCHIVED(
            "\uD83D\uDCDA",
            "Archived",
            4,
            Color.decode("#744F34")
    ),

    /** Indicates a task cancelled/terminated before completion. */
    CANCELLED(
            "\uD83D\uDEAB",
            "Cancelled",
            5,
            Color.decode("#FF0000")
    );

    /** Emoji or symbol that visually represents this status. */
    private final String ICON;

    /** Human-readable label for UI and embed displays. */
    private final String STRING_VALUE;

    /** Numeric code used for slash command options and stored enums. */
    private final int INT_VALUE;

    /** Associated embed color used in Discord messages. */
    private final Color COLOR_VALUE;

    /**
     * Constructs a new {@code TaskStatus} entry.
     *
     * @param icon         emoji or symbol representing this status
     * @param stringValue  human-readable label
     * @param intValue     numeric code for command options
     * @param colorValue   Discord embed color
     */
    TaskStatus(String icon, String stringValue, int intValue, Color colorValue) {
        this.ICON = icon;
        this.STRING_VALUE = stringValue;
        this.INT_VALUE = intValue;
        this.COLOR_VALUE = colorValue;
    }

    /** @return the emoji/symbol associated with this status */
    public String getIcon() {
        return ICON;
    }

    /** @return human-readable status label */
    public String getStringValue() {
        return STRING_VALUE;
    }

    /** @return numeric identifier for slash commands */
    public int getIntValue() {
        return INT_VALUE;
    }

    /** @return color used for embeds representing this status */
    public Color getColorValue() {
        return COLOR_VALUE;
    }

    /**
     * Retrieves a {@code TaskStatus} by its string label.
     *
     * <p><b>Note:</b> This comparison is case-sensitive and expects exact
     * match to {@link #STRING_VALUE}.</p>
     *
     *
     * @param stringValue the readable status string (e.g., "Completed")
     * @return matching status or {@code null} if none match
     */
    public static TaskStatus getTaskStatus(String stringValue) {
        return Arrays.stream(TaskStatus.values())
                .filter(taskStatus -> taskStatus.STRING_VALUE.equals(stringValue))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a {@code TaskStatus} using its numeric ID.
     *
     * <p>Mainly used when Discord slash command options return an integer.</p>
     *
     * @param intValue the numeric code assigned to each status
     * @return matching status or {@code null} if no match is found
     */
    public static TaskStatus getTaskStatus(int intValue) {
        return Arrays.stream(TaskStatus.values())
                .filter(taskStatus -> taskStatus.INT_VALUE == intValue)
                .findFirst()
                .orElse(null);
    }
}