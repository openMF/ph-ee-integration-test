package org.mifos.integrationtest.cucumber.stepdef;

import org.mifos.integrationtest.common.dto.paybill.PayBillRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PaybillStepDef {
    public String businessShortCode;
    public String billRefNo;
    public String msisdn;
    public String transactionId;
    public String amount;
    public String response;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public PayBillRequestDTO setPaybillRequestDTO() {
        PayBillRequestDTO payBillRequestDTO = new PayBillRequestDTO("Pay Bill", transactionId, "20191122063845", amount, businessShortCode, billRefNo, "", "49197.00", "", msisdn, "John");
        return payBillRequestDTO;
    }
}
