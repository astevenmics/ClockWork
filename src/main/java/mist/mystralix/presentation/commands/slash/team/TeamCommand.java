package mist.mystralix.presentation.commands.slash.team;

import mist.mystralix.application.helper.TaskHelper;
import mist.mystralix.application.pagination.PaginationService;
import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.team.TeamTaskService;
import mist.mystralix.presentation.commands.slash.SlashCommand;
import mist.mystralix.presentation.commands.slash.team.task.TeamTaskSubCommandFunctions;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.List;

public class TeamCommand implements SlashCommand {

    private final TeamSubCommandFunctions teamSubCommandFunctions;
    private final TeamTaskSubCommandFunctions teamTaskSubCommandFunctions;

    public TeamCommand(
            TeamTaskService teamTaskService,
            TeamService teamService,
            PaginationService paginationService
    ) {
        this.teamSubCommandFunctions = new TeamSubCommandFunctions(teamService, paginationService);
        this.teamTaskSubCommandFunctions = new TeamTaskSubCommandFunctions(teamTaskService, teamService, paginationService);
    }

    @Override
    public String getName() {
        return "team";
    }

    @Override
    public String getDescription() {
        return "Create and manage teams, control member roles, and collaborate through shared tasks.";
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of(
                new SubcommandData("create", "Create a new team and become its leader")
                    .addOptions(
                        new OptionData(
                            OptionType.STRING,
                            "team_name",
                            "Team name",
                            true
                        ).setRequiredLength(1, 32)
                ),
                new SubcommandData("delete", "Permanently delete a team you own.")
                    .addOptions(
                        new OptionData(
                            OptionType.INTEGER,
                            "id",
                            "Team ID of the team you want to delete",
                                true
                        )
                ),
                new SubcommandData("invite", "Invite a user to join your team.")
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
                new SubcommandData("kick", "Remove a member from your team.")
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
                new SubcommandData("invitation", "Accept or reject a team invitation you received.")
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
                new SubcommandData("leave", "Leave a team you are currently part of.")
                    .addOptions(
                        new OptionData(
                            OptionType.INTEGER,
                            "id",
                            "Team ID of the team you want to leave",
                            true
                        )
                ),
                new SubcommandData("position", "Change a member’s role within the team.")
                        .addOptions(
                        new OptionData(
                                OptionType.INTEGER,
                                "id",
                                "Team ID of the team you want to leave",
                                true
                        ),
                        new OptionData(
                                OptionType.USER,
                                "user",
                                "Mention a user to adjust their team position",
                                true
                        ),
                        new OptionData(
                                OptionType.STRING,
                                "position",
                                "Changes position of a member from either Leader, Moderator or Member",
                                true
                        )
                                .addChoice("Leader", "leader")
                                .addChoice("Moderator", "moderator")
                                .addChoice("Member", "member")
                ),
                new SubcommandData("name", "Rename an existing team you own.")
                        .addOptions(
                        new OptionData(
                                OptionType.INTEGER,
                                "id",
                                "Team ID of the team you want to leave",
                                true
                        ),
                        new OptionData(
                                OptionType.STRING,
                                "name",
                                "New name for the team",
                                true
                        )
                ),
                new SubcommandData("view", "View detailed information about a team by ID.")
                    .addOptions(
                        new OptionData(
                                OptionType.INTEGER,
                                "id",
                                "Team ID of the team you want to view",
                                true
                        )
                ),
                new SubcommandData("list", "View all teams you are currently a member of.")
        );
    }

    @Override
    public List<SubcommandGroupData> getSubcommandGroupData() {
        return List.of(
                new SubcommandGroupData("task", "Manage tasks that belong to a specific team.")
                        .addSubcommands(
                                new SubcommandData("create", "Create a new task for a team.")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true),
                                        new OptionData(OptionType.STRING, "title", "Task title", true),
                                        new OptionData(OptionType.STRING, "description", "Task description", true)
                                ),
                                new SubcommandData("delete", "Delete an existing task from a team.")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true),
                                        new OptionData(OptionType.INTEGER, "task", "Task ID", true)
                                ),
                                new SubcommandData("edit", "Edit a task’s title, description, or status.")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true),
                                        new OptionData(
                                                OptionType.INTEGER,
                                                "task",
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
                                        TaskHelper.getTaskTypeOptions()
                                ),
                                new SubcommandData("list", "View all tasks assigned to a team.")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true)
                                ),

                                new SubcommandData("assign", "Assign a task to a team member.")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true),
                                        new OptionData(OptionType.INTEGER, "task", "Task ID", true),
                                        new OptionData(OptionType.USER, "user", "User to assign", true)
                                ),

                                new SubcommandData("unassign", "Remove a task assignment from a team member.")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true),
                                        new OptionData(OptionType.INTEGER, "task", "Task ID", true),
                                        new OptionData(OptionType.USER, "user", "User to unassign", true)
                                ),
                                new SubcommandData("view", "View detailed information about a specific task.")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "team", "Team ID", true),
                                        new OptionData(OptionType.INTEGER, "task", "Task ID", true)
                                ))
        );
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

        if (subcommandGroup) {
            messageEmbed = switch (subCommand) {
                case "create" -> teamTaskSubCommandFunctions.create(event); // done
                case "delete" -> teamTaskSubCommandFunctions.delete(event); // done
                case "edit" -> teamTaskSubCommandFunctions.update(event); // done
                case "list" -> teamTaskSubCommandFunctions.readAll(event); // done
                case "assign", "unassign" -> teamTaskSubCommandFunctions.handleAssignment(event); // done
                case "view" -> teamTaskSubCommandFunctions.read(event); // done
                default -> null;
            };
        } else {
            messageEmbed = switch (subCommand) {
                case "create" -> teamSubCommandFunctions.create(event); // done
                case "delete" -> teamSubCommandFunctions.delete(event); // done
                case "invite" -> teamSubCommandFunctions.add(event); // done
                case "kick" -> teamSubCommandFunctions.remove(event); // done
                case "invitation" -> teamSubCommandFunctions.handleInvitation(event); // done
                case "leave" -> teamSubCommandFunctions.leave(event); // done
                case "view" -> teamSubCommandFunctions.read(event); // done
                case "list" -> teamSubCommandFunctions.readAll(event); // done
                case "position" -> teamSubCommandFunctions.handlePosition(event); // done
                case "name" -> teamSubCommandFunctions.updateName(event); // done
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