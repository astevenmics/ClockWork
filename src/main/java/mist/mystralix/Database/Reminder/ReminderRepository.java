package mist.mystralix.Database.Reminder;

import mist.mystralix.Objects.Reminder.Reminder;

import java.util.HashSet;
import java.util.List;

public interface ReminderRepository {

    void create(Reminder reminder);

    Reminder read(String userDiscordID, int reminderID);

    Reminder read(String userDiscordID, String reminderUUID);

    void update(Reminder reminder);

    void delete(String reminderUUID, String userDiscordID);

    List<Reminder> readAll(String userDiscordID);

    HashSet<Reminder> getAllActiveReminders();

    void updateIsNotificationSent(Reminder reminder);

}
