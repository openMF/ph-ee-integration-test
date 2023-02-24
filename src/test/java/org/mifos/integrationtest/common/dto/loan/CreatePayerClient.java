package org.mifos.integrationtest.common.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    public Object savingsProductId;
}
