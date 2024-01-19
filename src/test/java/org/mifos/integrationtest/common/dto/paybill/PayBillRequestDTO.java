package org.mifos.integrationtest.common.dto.paybill;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//{
//        "TransactionType":"Pay Bill",
//        "TransID":"RKTQDM7W6S",
//        "TransTime":"20191122063845",
//        "TransAmount":"10",
//        "BusinessShortCode":"24322607",
//        "BillRefNumber":"24322607",
//        "InvoiceNumber":"",
//        "OrgAccountBalance":"49197.00",
//        "ThirdPartyTransID":"",
//        "MSISDN":"254797668592",
//        "FirstName":"John"
//}
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayBillRequestDTO {

    @JsonProperty("TransactionType")
    public String transactionType;
    @JsonProperty("TransID")
    public String transID;
    @JsonProperty("TransTime")
    public String transTime;
    @JsonProperty("TransAmount")
    public String transAmount;
    @JsonProperty("BusinessShortCode")
    public String businessShortCode;
    @JsonProperty("BillRefNumber")
    public String billRefNumber;
    @JsonProperty("InvoiceNumber")
    public String invoiceNumber;
    @JsonProperty("OrgAccountBalance")
    public String orgAccountBalance;
    @JsonProperty("ThirdPartyTransID")
    public String thirdPartyTransID;
    @JsonProperty("MSISDN")
    public String msisdn;
    @JsonProperty("FirstName")
    public String firstName;
}
