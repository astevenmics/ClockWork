package mist.mystralix.application.pagination;

import java.util.HashMap;

public class PaginationService {

    private final HashMap<String, PaginationData> PAGINATION_DATA = new HashMap<>();

    public void addPaginationData(String userDiscordID, PaginationData paginationData) {
        PAGINATION_DATA.put(userDiscordID, paginationData);
    }

    public PaginationData getPaginationData(String userDiscordID) {
        return PAGINATION_DATA.get(userDiscordID);
    }

}
