package mist.mystralix.presentation.listeners;

import mist.mystralix.application.reminder.ReminderScheduler;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ShutdownHandler extends ListenerAdapter {

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        ReminderScheduler.getInstance().shutdown();
    }
}
