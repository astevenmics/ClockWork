package mist.mystralix.application.pagination;

import mist.mystralix.domain.reminder.Reminder;

import java.util.ArrayList;

public class ReminderPaginationData implements PaginationData {

    private int currentPage;
    private int totalPages;
    private ArrayList<Object> reminders;

    public ReminderPaginationData(int currentPage, int totalPages, ArrayList<Reminder> reminders) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.reminders = new ArrayList<>(reminders);
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
        return reminders;
    }

    @Override
    public void setData(ArrayList<Object> data) {
        this.reminders = data;
    }

}
