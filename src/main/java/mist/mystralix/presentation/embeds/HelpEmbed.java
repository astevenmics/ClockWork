package mist.mystralix.presentation.embeds;

import mist.mystralix.application.helper.StringHelper;
import mist.mystralix.presentation.commands.slash.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.awt.*;
import java.util.stream.Collectors;

public class HelpEmbed {

    public MessageEmbed createHelpFrontEmbed() {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("ClockWork | Information");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setDescription(
                """
                        ClockWork is a productivity and team-management Discord bot designed to help you stay organized, on time, and in control—whether you are working solo or collaborating with a team.
                        
                        At its core, ClockWork lets you manage reminders, personal tasks, and team-based workflows directly inside Discord using clean, easy-to-use slash commands.
                        """
        );
        embedBuilder.addBlankField(false);
        embedBuilder.addField(
                "⏰ Reminders",
                """
                        ClockWork acts like your personal assistant. You can create reminders with custom messages and time durations, view all your active reminders, update them if plans change, or delete them when they are no longer needed. Each reminder is tracked with a unique ID so you can easily manage or review specific ones at any time.
                        
                        ```md
                        You can:
                        - Create tasks with titles and descriptions
                        - Update task details or status as you make progress
                        - View individual tasks or list all tasks (with optional status filtering)
                        - Cancel or delete tasks when plans change```
                        """,
                false
        );
        embedBuilder.addBlankField(false);
        embedBuilder.addField(
                "✅ Tasks",
                """
                        ClockWork helps you track what you need to do and what is already done. This makes it easy to manage ongoing responsibilities without leaving Discord.
                        
                        ```md
                        Perfect for:
                        - Deadlines
                        - Events
                        - Study sessions
                        - “Do not forget” moments```
                        """,
                false
        );
        embedBuilder.addBlankField(false);
        embedBuilder.addField(
                "👥 Teams",
                """
                        ClockWork really shines when working with others. You can create and manage teams, invite users, remove members, leave teams, rename them, and even transfer leadership. Each team has its own structure, allowing members to collaborate efficiently.
                        
                        ```md
                        Within teams, you can:
                        - Assign tasks to specific members
                        - Unassign tasks when responsibilities shift
                        - Update or delete team tasks
                        - View all tasks associated with a team
                        - Adjust member positions or roles within the team
                        ```
                        ```md
                        This makes ClockWork ideal for:
                        - Project groups
                        - Gaming teams
                        - Study groups
                        - Small organizations or startups```
                        """,
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

        String commandName = StringHelper.capitalize(slashCommand.getName());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(commandName + " | Features");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setFooter("Type /help to view all categories and its commands.");

        for (SubcommandData sc : slashCommand.getSubcommands()) {
            embedBuilder.addField(
                    "/" + slashCommand.getName() + " " + sc.getName(),
                    sc.getDescription(),
                    false
            );
        }

        for (SubcommandGroupData group : slashCommand.getSubcommandGroupData()) {
            String subList = group.getSubcommands().stream()
                    .map(sub -> "• **" + sub.getName() + "** – " + sub.getDescription())
                    .collect(Collectors.joining("\n"));

            embedBuilder.addField(
                    "/" + slashCommand.getName() + " " + group.getName(),
                    subList,
                    false
            );
        }

        return embedBuilder.build();
    }

}
