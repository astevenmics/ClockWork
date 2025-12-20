package mist.mystralix.presentation.commands.slash.team;

import mist.mystralix.application.task.TaskService;
import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.team.TeamTaskService;
import mist.mystralix.domain.enums.TaskStatus;
import mist.mystralix.presentation.commands.slash.SlashCommand;
import mist.mystralix.presentation.commands.slash.team.task.TeamTaskSubCommandFunctions;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class TeamCommand implements SlashCommand {

    private final TeamTaskService TEAM_TASK_SERVICE;
    private final TeamService TEAM_SERVICE;
    private final TaskService TASK_SERVICE;

    public TeamCommand(
            TeamTaskService teamTaskService,
            TeamService teamService,
            TaskService taskService
    ) {
        this.TEAM_TASK_SERVICE = teamTaskService;
        this.TEAM_SERVICE = teamService;
        this.TASK_SERVICE = taskService;
    }

    @Override
    public String getName() {
        return "team";
    }

    @Override
    public String getDescription() {
        return "Ability to group and organize teams with tasks";
    }

    @Override
    public SubcommandData[] getSubcommands() {
        return new SubcommandData[]{
                new SubcommandData("create", "Create a team")
                    .addOptions(
                        new OptionData(
                            OptionType.STRING,
                            "team_name",
                            "Team name",
                            true
                        ).setRequiredLength(1, 32)
                ),
                new SubcommandData("delete", "Delete a team")
                    .addOptions(
                        new OptionData(
                            OptionType.INTEGER,
                            "id",
                            "Team ID of the team you want to delete",
                                true
                        )
                ),
                new SubcommandData("add", "Add a user in a team")
                    .addOptions(
                        new OptionData(
                            OptionType.INTEGER,
                            "id",
                            "Team ID of the team you want to modify",
                            true
                        ),
                        new OptionData(
                            OptionType.USER,
                            "user",
                            "Mention a user to add to team",
                            true
                        )
                ),
                new SubcommandData("remove", "Removes a user in a team")
                    .addOptions(
                        new OptionData(
                            OptionType.INTEGER,
                            "id",
                            "Team ID of the team you want to modify",
                            true
                        ),
                        new OptionData(
                            OptionType.USER,
                            "user",
                            "Mention a user to remove to team",
                            true
                        )
                ),
                new SubcommandData("invitation", "Respond to the invitation sent to you be a team.")
                    .addOptions(
                        new OptionData(
                            OptionType.INTEGER,
                            "id",
                            "Team ID of the team you received the invitation from.",
                            true
                        ),
                          new OptionData(
                                  OptionType.STRING,
                                  "decision",
                                  "Accept/Reject the invitation",
                                  true
                          )
                                  .addChoice("Accept", "accept")
                                  .addChoice("Reject", "reject")
                    ),
                new SubcommandData("leave", "Leave a team using team ID")
                    .addOptions(
                        new OptionData(
                            OptionType.INTEGER,
                            "id",
                            "Team ID of the team you want to leave",
                            true
                        )
                ),
                new SubcommandData("view", "View a team using team ID")
                    .addOptions(
                        new OptionData(
                                OptionType.INTEGER,
                                "id",
                                "Team ID of the team you want to view",
                                true
                        )
                ),
                new SubcommandData("list", "View all teams you are in")
        };
    }

    @Override
    public SubcommandGroupData[] getSubcommandGroupData() {
        return new SubcommandGroupData[]{
                new SubcommandGroupData("task", "Task Management")
                        .addSubcommands(
                        new SubcommandData("create", "Create a task")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true),
                                        new OptionData(OptionType.STRING, "title", "Task title", true),
                                        new OptionData(OptionType.STRING, "description", "Task description", true)
                                ),
                        new SubcommandData("delete", "Delete a task")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true),
                                        new OptionData(OptionType.INTEGER, "task", "Task ID", true)
                                ),
                        new SubcommandData("update", "Update a task’s title, description, or status.")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true),
                                        new OptionData(
                                                OptionType.INTEGER,
                                                "id",
                                                "ID of the task to update.",
                                                true
                                        ),
                                        new OptionData(
                                                OptionType.STRING,
                                                "title",
                                                "New title for the task.",
                                                false
                                        ).setRequiredLength(1, 32),
                                        new OptionData(
                                                OptionType.STRING,
                                                "description",
                                                "New description for the task.",
                                                false
                                        ).setRequiredLength(1, 256),
                                        new OptionData(
                                                OptionType.INTEGER,
                                                "type",
                                                "New status for the task.",
                                                false
                                        )
                                                .addChoice(
                                                        TaskStatus.COMPLETED.getIcon() + " " + TaskStatus.COMPLETED.getStringValue(),
                                                        TaskStatus.COMPLETED.getIntValue())
                                                .addChoice(
                                                        TaskStatus.INPROGRESS.getIcon() + " " + TaskStatus.INPROGRESS.getStringValue(),
                                                        TaskStatus.INPROGRESS.getIntValue())
                                                .addChoice(
                                                        TaskStatus.ARCHIVED.getIcon() + " " + TaskStatus.ARCHIVED.getStringValue(),
                                                        TaskStatus.ARCHIVED.getIntValue())
                                                .addChoice(
                                                        TaskStatus.CANCELLED.getIcon() + " " + TaskStatus.CANCELLED.getStringValue(),
                                                        TaskStatus.CANCELLED.getIntValue())
                                ),
                        new SubcommandData("list", "List all tasks")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true)
                                ),

                        new SubcommandData("assign", "Assign a task to a user")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true),
                                        new OptionData(OptionType.INTEGER, "task", "Task ID", true),
                                        new OptionData(OptionType.USER, "user", "User to assign", true)
                                ),

                        new SubcommandData("unassign", "Unassign a task from a user")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true),
                                        new OptionData(OptionType.INTEGER, "task", "Task ID", true),
                                        new OptionData(OptionType.USER, "user", "User to unassign", true)
                                ),

                        new SubcommandData("view", "View a task")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true),
                                        new OptionData(OptionType.INTEGER, "task", "Task ID", true)
                                ))
        };
    }

    @Override
    public void execute(SlashCommandInteraction event) {
        boolean subcommandGroup = event.getSubcommandGroup() != null;
        String subCommand = event.getSubcommandName();
        if (subCommand == null) {
            event.reply("Invalid subcommand.").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        MessageEmbed messageEmbed;

        TeamSubCommandFunctions teamSubCommandFunctions = new TeamSubCommandFunctions(TEAM_SERVICE);
        TeamTaskSubCommandFunctions teamTaskSubCommandFunctions = new TeamTaskSubCommandFunctions(
                TEAM_TASK_SERVICE,
                TEAM_SERVICE,
                TASK_SERVICE
        );

        if (subcommandGroup) {
            messageEmbed = switch (subCommand) {
                case "create" -> teamTaskSubCommandFunctions.create(event);
                case "delete" -> teamTaskSubCommandFunctions.create(event);
                case "update" -> teamTaskSubCommandFunctions.delete(event);
                case "list" -> teamTaskSubCommandFunctions.readAll(event);
                case "assign" -> teamTaskSubCommandFunctions.update(event);
                case "unassign" -> teamTaskSubCommandFunctions.read(event);
                case "view" -> teamTaskSubCommandFunctions.read(event);
                default -> null;
            };
        } else {
            messageEmbed = switch (subCommand) {
                case "create" -> teamSubCommandFunctions.create(event); // done
                case "delete" -> teamSubCommandFunctions.delete(event); // done
                case "add" -> teamSubCommandFunctions.add(event); // done
                case "remove" -> teamSubCommandFunctions.remove(event); // done
                case "invitation" -> teamSubCommandFunctions.handleInvitation(event); // done
                case "leave" -> teamSubCommandFunctions.leave(event); // done
                case "view" -> teamSubCommandFunctions.read(event); // done
                case "list" -> teamSubCommandFunctions.readAll(event); // done
                default -> null;
            };
        }

        if (messageEmbed == null) {
            event.getHook()
                    .editOriginal("❌ An unexpected error occurred (embed was null).")
                    .queue();
            return;
        }

        event.getHook().editOriginalEmbeds(messageEmbed).queue();

    }
}