package mist.mystralix.presentation.listeners;

import mist.mystralix.application.pagination.PaginationContext;
import mist.mystralix.application.pagination.PaginationData;
import mist.mystralix.application.pagination.PaginationEmbedCreator;
import mist.mystralix.application.pagination.PaginationService;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Map;

public class ButtonInteractionHandler extends ListenerAdapter {

    private final Map<Class<?>, PaginationContext> paginationContexts;

    public ButtonInteractionHandler(Map<Class<?>, PaginationContext> paginationContexts) {
        this.paginationContexts = paginationContexts;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        User user = event.getUser();
        if (user.isBot()) {
            return;
        }

        String[] parts = event.getComponentId().split(":");
        String action = parts[0];
        String className = parts[1];

        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ClassCastException("Class " + className + " not found");
        }

        PaginationService paginationService = paginationContexts.get(clazz).paginationService();
        PaginationEmbedCreator embedCreator = paginationContexts.get(clazz).embedCreator();
        if (paginationService == null || embedCreator == null) {
            return;
        }

        PaginationData paginationData = paginationService.getPaginationData(className + ":" + user.getId());
        if (paginationData == null) {
            return;
        }

        ArrayList<Object> objects = paginationData.getData();
        int currentPage = paginationData.getCurrentPage();
        int totalPages = paginationData.getTotalPages();
        int tasksPerPage = 12;

        if (action.equals("prev_page")) {
            currentPage--;
        } else if (action.equals("next_page")) {
            currentPage++;
        }

        Button nextButton = Button.primary("next_page:" + className, "Next");
        Button previousButton = Button.primary("prev_page:" + className, "Previous");

        MessageEmbed messageEmbed = embedCreator.createPaginatedEmbed(user, paginationData.getData(), currentPage, tasksPerPage);

        if (currentPage == 1) {
            previousButton = previousButton.asDisabled();
            nextButton = nextButton.asEnabled();
        } else if (currentPage == totalPages) {
            previousButton = previousButton.asEnabled();
            nextButton = nextButton.asDisabled();
        } else {
            previousButton = previousButton.asEnabled();
            nextButton = nextButton.asEnabled();
        }

        paginationData.setCurrentPage(currentPage);
        paginationData.setTotalPages(totalPages);
        paginationData.setData(objects);
        paginationService.addPaginationData(className + ":" + user.getId(), paginationData);

        event.deferEdit().queue();
        event.getHook().editOriginalEmbeds(messageEmbed).setComponents(ActionRow.of(previousButton, nextButton)).queue();
    }
}
