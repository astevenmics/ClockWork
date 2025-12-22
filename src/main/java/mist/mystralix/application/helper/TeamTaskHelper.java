package mist.mystralix.application.helper;

import mist.mystralix.domain.task.TeamTask;

public class TeamTaskHelper {

    public static void updateAssignedUsers(TeamTask teamTask, String userToHandleId, boolean isAssign) {
        if (isAssign) {
            teamTask.getAssignedUsers().add(userToHandleId);
        } else {
            teamTask.getAssignedUsers().remove(userToHandleId);
        }
    }

}
