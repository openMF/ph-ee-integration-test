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
public class SavingsActivate {
    public String activatedOnDate;
    public String locale;
    public String dateFormat;
}
