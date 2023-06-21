package org.mifos.integrationtest.common.dto.savings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SavingsApprove {

    public String approvedOnDate;
    public String locale;
    public String dateFormat;

    public SavingsApprove(String approvedOnDate, String locale, String dateFormat) {
        this.approvedOnDate = approvedOnDate;
        this.locale = locale;
        this.dateFormat = dateFormat;
    }
}
