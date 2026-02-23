package com.example.demo.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public class PaginatedData<T> {

    private List<T> list;
    private long total;

    public PaginatedData(Page<T> page) {
        this.list = page.getContent();
        this.total = page.getTotalElements();
    }

    public PaginatedData(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
