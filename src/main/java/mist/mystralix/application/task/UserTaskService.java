package mist.mystralix.application.task;

import mist.mystralix.domain.task.UserTask;
import mist.mystralix.infrastructure.repository.task.UserTaskRepository;
import mist.mystralix.utils.IdentifiableFetcher;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class UserTaskService implements IdentifiableFetcher<UserTask> {

    private final UserTaskRepository USER_TASK_REPOSITORY;

    public UserTaskService(UserTaskRepository userTaskRepository) {
        this.USER_TASK_REPOSITORY = userTaskRepository;
    }

    @Override
    public void update(UserTask task) {
        USER_TASK_REPOSITORY.update(task);
    }

    @Override
    public void delete(UserTask task) {
        USER_TASK_REPOSITORY.delete(task);
    }

    @Override
    public UserTask getById(int id) {
        return USER_TASK_REPOSITORY.findByID(id);
    }

    @Override
    public UserTask getByUUID(String uuid) {
        return USER_TASK_REPOSITORY.findByUUID(uuid);
    }

    public void addTask(User user, String uuid, String title, String description, int status) {
        USER_TASK_REPOSITORY.create(
                new UserTask.Builder(uuid)
                        .userDiscordID(user.getId())
                        .title(title)
                        .description(description)
                        .status(status)
                        .build()
        );
    }

    public ArrayList<UserTask> getUserTasks(User user) {
        return USER_TASK_REPOSITORY.readAll(user.getId());
    }

}