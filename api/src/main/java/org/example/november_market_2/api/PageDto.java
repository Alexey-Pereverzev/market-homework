package org.example.november_market_2.api;

import java.util.List;

public class PageDto {
    private int totalPages;
    private int number;
    private List<ProductDto> content;

    public PageDto() {
    }

    public PageDto(int totalPages, int number, List<ProductDto> content) {
        this.totalPages = totalPages;
        this.number = number;
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<ProductDto> getContent() {
        return content;
    }

    public void setContent(List<ProductDto> content) {
        this.content = content;
    }
}
