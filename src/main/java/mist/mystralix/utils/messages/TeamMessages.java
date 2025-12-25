package mist.mystralix.utils.messages;

public final class TeamMessages {

    public static final String NO_CURRENT_TEAM_TASK =
            "There are currently no Team Task for this team.";

    public static final String TEAM_TASK_NOT_PART_OF_TEAM =
            "Team Task #%d is not part of the %s team.";

    public static final String USER_ALREADY_INVITED =
            "The user, %s, has already been invited.";

    public static final String USER_ALREADY_PART_OF_TEAM =
            "The user, %s, is already part of the %s team";

    public static final String USER_NOT_PART_OF_TEAM =
            "The mentioned user is not a part of the **%s** team.";

    public static final String USER_ALREADY_HAS_ROLE_IN_TEAM =
            "The user, %s, is already a %s in the %s team";

    public static final String ALREADY_PART_OF_TEAM =
            "You are already part of the **%s** team";

    public static final String NOT_IN_A_TEAM =
            "You are currently not in a team.";

    public static final String NOT_PART_OF_TEAM =
            "You are not a part of the **%s** team.";

    public static final String TEAM_DELETED =
            "**%s** team has been deleted";

    public static final String USER_ALREADY_ASSIGNED_TASK =
            "The user, %s, is assigned for this team task.";

    public static final String USER_NOT_ASSIGNED_TASK =
            "The user, %s, is not assigned for this team task.";

    public static final String NO_PENDING_TEAM_INVITATION =
            "You do not have any pending invitations for this team";

    public static final String NO_MODERATORS =
            "No moderators";

    public static final String NO_MEMBERS =
            "No members";

    public static final String CANNOT_REMOVE_FELLOW_MODERATOR =
            "You are not able to remove co-moderators from the team.";

    public static final String LEADER_CANNOT_LEAVE =
            "You cannot leave the team while you're still the leader.\n" +
                    "You must appoint someone else as the leader before departing.";

    public static final String LEADER_REQUIRED =
            "You must be the team leader to execute this command.";

    public static final String MODERATOR_REQUIRED =
            "You must be a team moderator or higher to execute this command.";

    private TeamMessages() {
    }

}
