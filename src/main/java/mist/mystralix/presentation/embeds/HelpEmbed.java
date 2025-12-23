package mist.mystralix.presentation.embeds;

import mist.mystralix.presentation.commands.slash.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.awt.*;

public class HelpEmbed {

    public MessageEmbed createCategoryEmbed(SlashCommand slashCommand) {

        String command = slashCommand.getName();
        String commandName = command.substring(0, 1).toUpperCase() + command.substring(1);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(commandName + " | Features");

        for (SubcommandData sc : slashCommand.getSubcommands()) {
            embedBuilder.addField(
                    "/" + slashCommand.getName() + " " + sc.getName(),
                    sc.getDescription(),
                    false
            );
        }

        for (SubcommandGroupData group : slashCommand.getSubcommandGroupData()) {
            StringBuilder subList = new StringBuilder();

            for (SubcommandData sub : group.getSubcommands()) {
                subList.append("• **")
                        .append(sub.getName())
                        .append("** – ")
                        .append(sub.getDescription())
                        .append("\n");
            }

            embedBuilder.addField(
                    "/" + slashCommand.getName() + " " + group.getName(),
                    subList.toString(),
                    false
            );
        }


        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setFooter("Type /help to view all categories and its commands.");

        return embedBuilder.build();
    }

}
