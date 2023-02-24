package org.mifos.integrationtest.common.dto.savings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SavingsApprove {
    public String approvedOnDate;
    public String locale;
    public String dateFormat;
}
