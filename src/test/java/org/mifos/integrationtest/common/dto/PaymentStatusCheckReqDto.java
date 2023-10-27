package org.mifos.integrationtest.common.dto;

import java.util.List;

public class PaymentStatusCheckReqDto {

    @Override
    public String toString() {
        return "PaymentStatusCheckReqDto{" + "requestIds:" + requestIds + ", payeePartyIds:" + payeePartyIds + '}';
    }

    List<String> requestIds;
    List<String> payeePartyIds;

    public PaymentStatusCheckReqDto() {

    }

    public List<String> getRequestIds() {
        return requestIds;
    }

    public void setRequestIds(List<String> requestIds) {
        this.requestIds = requestIds;
    }

    public List<String> getPayeePartyIds() {
        return payeePartyIds;
    }

    public void setPayeePartyIds(List<String> payeePartyIds) {
        this.payeePartyIds = payeePartyIds;
    }
}
