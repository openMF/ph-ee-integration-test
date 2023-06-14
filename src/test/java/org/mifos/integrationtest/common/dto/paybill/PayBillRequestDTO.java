package org.mifos.integrationtest.common.dto.paybill;

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

    public String TransactionType;
    public String TransID;
    public String TransTime;
    public String TransAmount;
    public String BusinessShortCode;
    public String BillRefNumber;
    public String InvoiceNumber;
    public String OrgAccountBalance;
    public String ThirdPartyTransID;
    public String MSISDN;
    public String FirstName;
}
