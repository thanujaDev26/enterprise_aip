package com.copperleaf.asset_invesment_planner.dto;

import java.util.List;

public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages) {
        this.content = content; this.page = page; this.size = size;
        this.totalElements = totalElements; this.totalPages = totalPages;
    }
}
