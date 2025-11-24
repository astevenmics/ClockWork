package mist.mystralix.Objects.Reminder;

// TODO: Add Comments
public class Reminder {

    public String reminderUUID;
    public String message;
    public int reminderID;
    public long timestamp;

    public Reminder(
            String reminderUUID,
            String message,
            int reminderID,
            long timestamp
    ) {
        this.reminderUUID = reminderUUID;
        this.message = message;
        this.reminderID = reminderID;
        this.timestamp = timestamp;
    }

}