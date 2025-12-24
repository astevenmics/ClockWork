package mist.mystralix.application.pagination;

import mist.mystralix.domain.task.TeamTask;

import java.util.ArrayList;

public class TeamTaskPaginationData implements PaginationData {

    private int currentPage;
    private int totalPages;
    private ArrayList<Object> teamTasks;

    public TeamTaskPaginationData(int currentPage, int totalPages, ArrayList<TeamTask> teamTasks) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.teamTasks = new ArrayList<>(teamTasks);
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
        return teamTasks;
    }

    @Override
    public void setData(ArrayList<Object> data) {
        this.teamTasks = data;
    }

}
