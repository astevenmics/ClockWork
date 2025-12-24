package mist.mystralix.application.pagination;

import mist.mystralix.domain.task.Task;

import java.util.ArrayList;

public class TaskPaginationData implements PaginationData {

    private int currentPage;
    private int totalPages;
    private ArrayList<Object> tasks;

    public TaskPaginationData(int currentPage, int totalPages, ArrayList<Task> tasks) {
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
