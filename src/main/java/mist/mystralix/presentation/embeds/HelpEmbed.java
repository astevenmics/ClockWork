package mist.mystralix.presentation.embeds;

import mist.mystralix.presentation.commands.slash.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.awt.*;

public class HelpEmbed {

    public MessageEmbed createHelpFrontEmbed() {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("ClockWork | Information");
        embedBuilder.setDescription(
                """
                        ClockWork is a productivity and team-management Discord bot designed to help you stay organized, on time, and in control—whether you’re working solo or collaborating with a team.
                        
                        At its core, ClockWork lets you manage reminders, personal tasks, and team-based workflows directly inside Discord using clean, easy-to-use slash commands.
                        """);
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.addBlankField(false);
        embedBuilder.addField(
                "⏰ Reminders",
                "ClockWork acts like your personal assistant. You can create reminders with custom messages and time durations, view all your active reminders, update them if plans change, or delete them when they are no longer needed. Each reminder is tracked with a unique ID so you can easily manage or review specific ones at any time." +
                        "\n```md\n" +
                        "You can:\n" +
                        "- Create tasks with titles and descriptions\n" +
                        "- Update task details or status as you make progress\n" +
                        "- View individual tasks or list all tasks (with optional status filtering)\n" +
                        "- Cancel or delete tasks when plans change```",
                false
        );
        embedBuilder.addBlankField(false);
        embedBuilder.addField(
                "✅ Tasks",
                "ClockWork helps you track what you need to do and what’s already done. This makes it easy to manage ongoing responsibilities without leaving Discord." +
                        "\n```md\n" +
                        "Perfect for:\n" +
                        "- Deadlines\n" +
                        "- Events\n" +
                        "- Study sessions\n" +
                        "- “Don’t forget” moments```",
                false
        );
        embedBuilder.addBlankField(false);
        embedBuilder.addField(
                "👥 Teams",
                "ClockWork really shines when working with others. You can create and manage teams, invite users, remove members, leave teams, rename them, and even transfer leadership. Each team has its own structure, allowing members to collaborate efficiently." +
                        "\n```md\n" +
                        "Within teams, you can:\n" +
                        "- Assign tasks to specific members\n" +
                        "- Unassign tasks when responsibilities shift\n" +
                        "- Update or delete team tasks\n" +
                        "- View all tasks associated with a team\n" +
                        "- Adjust member positions or roles within the team\n```" +
                        "```md\n" +
                        "This makes ClockWork ideal for:\n" +
                        "- Project groups\n" +
                        "- Gaming teams\n" +
                        "- Study groups\n" +
                        "- Small organizations or startups```",
                false
        );
        embedBuilder.addBlankField(false);
        embedBuilder.addField(
                "🧭 Simple, Structured, and Transparent",
                "Every reminder, task, and team action is organized using IDs, making it easy to view, update, or manage specific items without confusion. The bot keeps everything centralized in Discord—no external apps, no clutter.",
                false
        );
        embedBuilder.setFooter("TL;DR | Invite ClockWork to your server");
        return embedBuilder.build();
    }

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
