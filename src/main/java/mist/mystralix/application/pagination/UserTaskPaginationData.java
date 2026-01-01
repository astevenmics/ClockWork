package mist.mystralix.application.pagination;

import mist.mystralix.domain.task.UserTask;

import java.util.ArrayList;

public class UserTaskPaginationData implements PaginationData {

    private int currentPage;
    private int totalPages;
    private ArrayList<Object> tasks;

    public UserTaskPaginationData(int currentPage, int totalPages, ArrayList<UserTask> tasks) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.tasks = new ArrayList<>(tasks);
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
        return tasks;
    }

    @Override
    public void setData(ArrayList<Object> data) {
        this.tasks = data;
    }

}
