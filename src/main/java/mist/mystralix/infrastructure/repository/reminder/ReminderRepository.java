package mist.mystralix.infrastructure.repository.reminder;

import mist.mystralix.infrastructure.repository.base.BaseRepository;
import mist.mystralix.domain.reminder.Reminder;

import java.util.HashSet;

public interface ReminderRepository extends BaseRepository<Reminder> {

    HashSet<Reminder> getAllActiveReminders();

    void updateIsNotificationSent(Reminder reminder);

}