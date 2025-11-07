package mist.mystralix.Listeners.MessageListeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReceived extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if((event.getAuthor().isBot()) || (event.getMember() == null)) { return; }
    }
}