package mist.mystralix.application.pagination;

import java.util.ArrayList;

public interface PaginationData {

    int getCurrentPage();

    void setCurrentPage(int currentPage);

    int getTotalPages();

    void setTotalPages(int totalPages);

    ArrayList<Object> getData();

    void setData(ArrayList<Object> data);

}
