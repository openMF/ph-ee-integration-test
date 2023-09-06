package org.mifos.integrationtest.common.dto.operationsapp;

import java.util.ArrayList;

public class GetTransactionRequestResponse {

    private ArrayList<TransactionRequest> content;
    private int totalPages;
    private int totalElements;
    private boolean last;
    private int numberOfElements;
    private Sort sort;
    private boolean first;
    private int size;
    private int number;

    public GetTransactionRequestResponse() {}

    public ArrayList<TransactionRequest> getContent() {
        return content;
    }

    public void setContent(ArrayList<TransactionRequest> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
