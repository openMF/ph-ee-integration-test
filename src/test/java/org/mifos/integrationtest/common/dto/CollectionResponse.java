package org.mifos.integrationtest.common.dto;

public class CollectionResponse {

    private String transactionId;

    public CollectionResponse(String transactionId) {
        this.transactionId = transactionId;
    }

    public CollectionResponse() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
