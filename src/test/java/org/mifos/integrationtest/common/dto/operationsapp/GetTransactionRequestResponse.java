package org.mifos.integrationtest.common.dto.operationsapp;

import io.cucumber.java.eo.Se;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetTransactionRequestResponse {

    private ArrayList<TransactionRequest> content;
    private int totalPages;
    private int totalElements;
    private boolean last;
    private int numberOfElements;
    private ArrayList<Sort> sort;
    private boolean first;
    private int size;
    private int number;
}
