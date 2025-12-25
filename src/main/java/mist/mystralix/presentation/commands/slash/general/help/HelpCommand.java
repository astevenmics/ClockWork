package mist.mystralix.presentation.commands.slash.general.help;

import mist.mystralix.presentation.commands.slash.SlashCommand;
import mist.mystralix.presentation.embeds.HelpEmbed;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public final class HelpCommand implements SlashCommand {

    private final HelpEmbed HELP_EMBED = new HelpEmbed();

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
                .addOption("ClockWork Information", "information")
                .addOption("Reminder Commands", "reminder")
                .addOption("Task Commands", "task")
                .addOption("Team Commands", "team")
                .build();

        MessageEmbed messageEmbed = HELP_EMBED.createHelpFrontEmbed();

        event.replyEmbeds(messageEmbed)
                .addComponents(ActionRow.of(menu))
                .queue();
    }

}