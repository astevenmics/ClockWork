package mist.mystralix.presentation.listeners;

import mist.mystralix.presentation.commands.slash.SlashCommand;
import mist.mystralix.presentation.embeds.GeneralEmbed;
import mist.mystralix.presentation.embeds.HelpEmbed;
import mist.mystralix.utils.messages.UserMessages;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class StringSelectInteractionHandler extends ListenerAdapter {

    private final HashMap<String, SlashCommand> COMMANDS;

    private final HelpEmbed HELP_EMBED = new HelpEmbed();

    public StringSelectInteractionHandler(HashMap<String, SlashCommand> commands) {
        this.COMMANDS = commands;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {

        User user = event.getUser();
        String[] parts = event.getComponentId().split(":");
        if (parts.length != 2) {
            return;
        }
        String commandName = parts[0];
        String userDiscordID = parts[1];
        if (!commandName.equals("help")) {
            return;
        }

        if (!user.getId().equals(userDiscordID)) {
            event.replyEmbeds(
                            GeneralEmbed.createErrorEmbed(
                                    String.format(UserMessages.MENU_BELONGS_TO_OTHER_USER, user.getAsMention())
                            ))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String selectedOption = event.getInteraction().getSelectedOptions().getFirst().getValue();

        MessageEmbed messageEmbed = switch (selectedOption) {
            case "information" -> HELP_EMBED.createHelpFrontEmbed();
            case "reminder", "task", "team" -> HELP_EMBED.createCategoryEmbed(COMMANDS.get(selectedOption));
            default -> null;
        };

        event.deferEdit().queue();
        event.getHook().editOriginalEmbeds(messageEmbed).queue();

    }

}
