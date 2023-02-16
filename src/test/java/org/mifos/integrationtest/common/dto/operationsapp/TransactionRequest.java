package org.mifos.integrationtest.common.dto.operationsapp;

public class TransactionRequest {
    private int id;
    private String workflowInstanceKey;
    private String transactionId;
    private long startedAt;
    private Object completedAt;
    private String state;
    private Object payeeDfspId;
    private String payeePartyId;
    private String payeePartyIdType;
    private Object payeeFee;
    private Object payeeQuoteCode;
    private Object payerDfspId;
    private String payerPartyId;
    private String payerPartyIdType;
    private Object payerFee;
    private Object payerQuoteCode;
    private int amount;
    private String currency;
    private String direction;
    private Object authType;
    private String initiatorType;
    private String scenario;

    private String externalId;

    private String clientCorrelationId;

    public TransactionRequest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkflowInstanceKey() {
        return workflowInstanceKey;
    }

    public void setWorkflowInstanceKey(String workflowInstanceKey) {
        this.workflowInstanceKey = workflowInstanceKey;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public Object getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Object completedAt) {
        this.completedAt = completedAt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Object getPayeeDfspId() {
        return payeeDfspId;
    }

    public void setPayeeDfspId(Object payeeDfspId) {
        this.payeeDfspId = payeeDfspId;
    }

    public String getPayeePartyId() {
        return payeePartyId;
    }

    public void setPayeePartyId(String payeePartyId) {
        this.payeePartyId = payeePartyId;
    }

    public String getPayeePartyIdType() {
        return payeePartyIdType;
    }

    public void setPayeePartyIdType(String payeePartyIdType) {
        this.payeePartyIdType = payeePartyIdType;
    }

    public Object getPayeeFee() {
        return payeeFee;
    }

    public void setPayeeFee(Object payeeFee) {
        this.payeeFee = payeeFee;
    }

    public Object getPayeeQuoteCode() {
        return payeeQuoteCode;
    }

    public void setPayeeQuoteCode(Object payeeQuoteCode) {
        this.payeeQuoteCode = payeeQuoteCode;
    }

    public Object getPayerDfspId() {
        return payerDfspId;
    }

    public void setPayerDfspId(Object payerDfspId) {
        this.payerDfspId = payerDfspId;
    }

    public String getPayerPartyId() {
        return payerPartyId;
    }

    public void setPayerPartyId(String payerPartyId) {
        this.payerPartyId = payerPartyId;
    }

    public String getPayerPartyIdType() {
        return payerPartyIdType;
    }

    public void setPayerPartyIdType(String payerPartyIdType) {
        this.payerPartyIdType = payerPartyIdType;
    }

    public Object getPayerFee() {
        return payerFee;
    }

    public void setPayerFee(Object payerFee) {
        this.payerFee = payerFee;
    }

    public Object getPayerQuoteCode() {
        return payerQuoteCode;
    }

    public void setPayerQuoteCode(Object payerQuoteCode) {
        this.payerQuoteCode = payerQuoteCode;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Object getAuthType() {
        return authType;
    }

    public void setAuthType(Object authType) {
        this.authType = authType;
    }

    public String getInitiatorType() {
        return initiatorType;
    }

    public void setInitiatorType(String initiatorType) {
        this.initiatorType = initiatorType;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getClientCorrelationId() {
        return clientCorrelationId;
    }

    public void setClientCorrelationId(String clientCorrelationId) {
        this.clientCorrelationId = clientCorrelationId;
    }
}
