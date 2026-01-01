package mist.mystralix.presentation.commands.manager;

import mist.mystralix.ClockWorkContainer;
import mist.mystralix.application.pagination.PaginationContext;
import mist.mystralix.domain.reminder.Reminder;
import mist.mystralix.domain.task.TeamTask;
import mist.mystralix.domain.task.UserTask;
import mist.mystralix.domain.team.Team;
import mist.mystralix.presentation.commands.slash.SlashCommand;
import mist.mystralix.presentation.embeds.ReminderEmbed;
import mist.mystralix.presentation.embeds.TeamEmbed;
import mist.mystralix.presentation.embeds.TeamTaskEmbed;
import mist.mystralix.presentation.embeds.UserTaskEmbed;
import mist.mystralix.presentation.listeners.ButtonInteractionHandler;
import mist.mystralix.presentation.listeners.ShutdownHandler;
import mist.mystralix.presentation.listeners.SlashCommandInteractionHandler;
import mist.mystralix.presentation.listeners.StringSelectInteractionHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class ListenerManager extends ListenerAdapter {

    public ListenerManager(JDA jda, HashMap<String, SlashCommand> commands, ClockWorkContainer container) {
        jda.addEventListener(new ShutdownHandler());
        jda.addEventListener(new SlashCommandInteractionHandler(commands));
        jda.addEventListener(new StringSelectInteractionHandler(commands));

        Map<Class<?>, PaginationContext> paginationContexts = new HashMap<>();
        paginationContexts.put(Reminder.class, new PaginationContext(container.getPaginationService(), new ReminderEmbed()));
        paginationContexts.put(UserTask.class, new PaginationContext(container.getPaginationService(), new UserTaskEmbed()));
        paginationContexts.put(Team.class, new PaginationContext(container.getPaginationService(), new TeamEmbed()));
        paginationContexts.put(TeamTask.class, new PaginationContext(container.getPaginationService(), new TeamTaskEmbed()));

        jda.addEventListener(new ButtonInteractionHandler(paginationContexts));
    }
}