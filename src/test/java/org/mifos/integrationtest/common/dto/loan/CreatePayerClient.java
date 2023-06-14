package org.mifos.integrationtest.common.dto.loan;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreatePayerClient {

    public ArrayList<Object> address;
    public ArrayList<Object> familyMembers;
    public int officeId;
    public int legalFormId;
    public String firstname;
    public String lastname;
    public boolean active;
    public String locale;
    public String dateFormat;
    public String activationDate;
    public String submittedOnDate;

    public CreatePayerClient(ArrayList<Object> address, ArrayList<Object> familyMembers, int officeId, int legalFormId, String firstname,
            String lastname, boolean active, String locale, String dateFormat, String activationDate, String submittedOnDate,
            Object savingsProductId) {
        this.address = address;
        this.familyMembers = familyMembers;
        this.officeId = officeId;
        this.legalFormId = legalFormId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.active = active;
        this.locale = locale;
        this.dateFormat = dateFormat;
        this.activationDate = activationDate;
        this.submittedOnDate = submittedOnDate;
        this.savingsProductId = savingsProductId;
    }

    public Object savingsProductId;
}
