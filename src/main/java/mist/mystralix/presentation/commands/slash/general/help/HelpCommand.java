package mist.mystralix.presentation.commands.slash.general.help;

import mist.mystralix.application.reminder.ReminderService;
import mist.mystralix.application.task.TaskService;
import mist.mystralix.application.team.TeamService;
import mist.mystralix.application.team.TeamTaskService;
import mist.mystralix.presentation.commands.slash.SlashCommand;
import mist.mystralix.presentation.commands.slash.reminder.ReminderCommand;
import mist.mystralix.presentation.commands.slash.task.TaskCommand;
import mist.mystralix.presentation.commands.slash.team.TeamCommand;
import mist.mystralix.presentation.embeds.GeneralEmbed;
import mist.mystralix.presentation.embeds.HelpEmbed;
import mist.mystralix.utils.Constants;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public final class HelpCommand implements SlashCommand {

    private final TeamTaskService TEAM_TASK_SERVICE;
    private final TaskService TASK_SERVICE;
    private final ReminderService REMINDER_SERVICE;
    private final TeamService TEAM_SERVICE;

    public HelpCommand(
            TeamTaskService teamTaskService,
            TaskService taskService,
            ReminderService reminderService,
            TeamService teamService
    ) {
        this.TEAM_TASK_SERVICE = teamTaskService;
        this.TASK_SERVICE = taskService;
        this.REMINDER_SERVICE = reminderService;
        this.TEAM_SERVICE = teamService;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows information on the bot and the list of available commands";
    }

    @Override
    public void execute(SlashCommandInteraction event) {

        StringSelectMenu menu = StringSelectMenu
                .create("help:" + event.getUser().getId())
                .setPlaceholder("Click here to see a list of available categories")
                .addOption("Reminder Commands", "reminder")
                .addOption("Task Commands", "task")
                .addOption("Team Commands", "team")
                .build();

        event.reply("Choose a category to see help details.")
                .addComponents(
                        ActionRow.of(menu)
                )
                .queue();
    }

    @Override
    public void stringSelectInteraction(StringSelectInteractionEvent event) {

        User user = event.getUser();
        String[] parts = event.getComponentId().split(":");
        String commandName = parts[0];
        String userDiscordID = parts[1];
        if (!commandName.equals(getName())) {
            return;
        }

        if (!user.getId().equals(userDiscordID)) {
            event.replyEmbeds(
                            GeneralEmbed.createErrorEmbed(
                                    String.format(
                                            Constants.MENU_BELONGS_TO_OTHER_USER.getValue(String.class),
                                            user.getAsMention()
                                    )))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String selectedOption = event.getInteraction().getSelectedOptions().getFirst().getValue();

        HelpEmbed helpEmbed = new HelpEmbed();
        MessageEmbed messageEmbed = switch (selectedOption) {
            case "reminder" -> helpEmbed.createCategoryEmbed(new ReminderCommand(REMINDER_SERVICE));
            case "task" -> helpEmbed.createCategoryEmbed(new TaskCommand(TASK_SERVICE));
            case "team" -> helpEmbed.createCategoryEmbed(new TeamCommand(TEAM_TASK_SERVICE, TEAM_SERVICE));
            default -> null;
        };

        event.deferEdit().queue();

        event.getHook().editOriginalEmbeds(messageEmbed).queue();

    }
}