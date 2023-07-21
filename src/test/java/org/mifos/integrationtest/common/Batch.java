package org.mifos.integrationtest.common;

import java.util.Date;

public class Batch {

    private String batchId;

    private String subBatchId;

    private String requestId;

    private String requestFile;

    private Long totalTransactions;

    private Long ongoing;

    private Long failed;

    private Long completed;

    private Long totalAmount;

    private Long ongoingAmount;

    private Long failedAmount;

    private Long completedAmount;

    private String result_file;

    private Date resultGeneratedAt;

    private String note;

    private Long workflowKey;

    private Long workflowInstanceKey;

    private Date startedAt;

    private Date completedAt;

    private String paymentMode;

    public Batch() {
    }

    public Batch(Long workflowInstanceKey) {
        this.workflowInstanceKey = workflowInstanceKey;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestFile() {
        return requestFile;
    }

    public void setRequestFile(String requestFile) {
        this.requestFile = requestFile;
    }

    public Long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(Long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public Long getOngoing() {
        return ongoing;
    }

    public void setOngoing(Long ongoing) {
        this.ongoing = ongoing;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }

    public Long getCompleted() {
        return completed;
    }

    public void setCompleted(Long completed) {
        this.completed = completed;
    }

    public String getResult_file() {
        return result_file;
    }

    public void setResult_file(String result_file) {
        this.result_file = result_file;
    }

    public Date getResultGeneratedAt() {
        return resultGeneratedAt;
    }

    public void setResultGeneratedAt(Date resultGeneratedAt) {
        this.resultGeneratedAt = resultGeneratedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getWorkflowKey() {
        return workflowKey;
    }

    public void setWorkflowKey(Long workflowKey) {
        this.workflowKey = workflowKey;
    }

    public Long getWorkflowInstanceKey() {
        return workflowInstanceKey;
    }

    public void setWorkflowInstanceKey(Long workflowInstanceKey) {
        this.workflowInstanceKey = workflowInstanceKey;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public String getSubBatchId() {
        return subBatchId;
    }

    public void setSubBatchId(String subBatchId) {
        this.subBatchId = subBatchId;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getOngoingAmount() {
        return ongoingAmount;
    }

    public void setOngoingAmount(Long ongoingAmount) {
        this.ongoingAmount = ongoingAmount;
    }

    public Long getFailedAmount() {
        return failedAmount;
    }

    public void setFailedAmount(Long failedAmount) {
        this.failedAmount = failedAmount;
    }

    public Long getCompletedAmount() {
        return completedAmount;
    }

    public void setCompletedAmount(Long completedAmount) {
        this.completedAmount = completedAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String mode) {
        this.paymentMode = mode;
    }
}

