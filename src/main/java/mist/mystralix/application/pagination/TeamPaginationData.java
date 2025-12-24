package mist.mystralix.application.pagination;

import mist.mystralix.domain.team.Team;

import java.util.ArrayList;

public class TeamPaginationData implements PaginationData {

    private int currentPage;
    private int totalPages;
    private ArrayList<Object> teams;

    public TeamPaginationData(int currentPage, int totalPages, ArrayList<Team> teams) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.teams = new ArrayList<>(teams);
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public ArrayList<Object> getData() {
        return teams;
    }

    @Override
    public void setData(ArrayList<Object> data) {
        this.teams = data;
    }

}
