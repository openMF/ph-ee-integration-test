package org.mifos.integrationtest.common.dto.billpayp2g;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class BillPaymentsReqDTO implements Serializable {

    @Override
    public String toString() {
        return "BillPaymentsReqDTO{" + "billInquiryRequestId='" + billInquiryRequestId + '\'' + ", billId='" + billId + '\''
                + ", paymentReferenceID='" + paymentReferenceID + '\'' + '}';
    }

    private String billInquiryRequestId;
    private String billId;
    private String paymentReferenceID;

    private String clientCorrelationId;

    public String getClientCorrelationId() {
        return clientCorrelationId;
    }

    public void setClientCorrelationId(String clientCorrelationId) {
        this.clientCorrelationId = clientCorrelationId;
    }

    public String getBillInquiryRequestId() {
        return billInquiryRequestId;
    }

    public void setBillInquiryRequestId(String billInquiryRequestId) {
        this.billInquiryRequestId = billInquiryRequestId;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getPaymentReferenceID() {
        return paymentReferenceID;
    }

    public void setPaymentReferenceID(String paymentReferenceID) {
        this.paymentReferenceID = paymentReferenceID;
    }

}
