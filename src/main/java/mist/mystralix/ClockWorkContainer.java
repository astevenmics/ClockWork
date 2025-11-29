package mist.mystralix;

import mist.mystralix.Database.Reminder.DBReminderRepository;
import mist.mystralix.Database.Task.DBTaskRepository;
import mist.mystralix.Database.Reminder.ReminderRepository;
import mist.mystralix.Database.Task.TaskRepository;
import mist.mystralix.Objects.Reminder.ReminderService;
import mist.mystralix.Objects.Task.TaskService;

public class ClockWorkContainer {

    private final TaskService taskService;
    private final ReminderService reminderService;

    public ClockWorkContainer() {
        TaskRepository taskRepository = new DBTaskRepository();
        ReminderRepository reminderRepository = new DBReminderRepository();
        this.taskService = new TaskService(taskRepository);
        this.reminderService = new ReminderService(reminderRepository);
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public ReminderService getReminderService() {
        return reminderService;
    }

}
