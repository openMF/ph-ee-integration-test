package org.mifos.integrationtest.common.dto.savings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SavingsActivate {
    public String activatedOnDate;
    public String locale;
    public String dateFormat;

    public SavingsActivate(String activatedOnDate, String locale, String dateFormat) {
        this.activatedOnDate = activatedOnDate;
        this.locale = locale;
        this.dateFormat = dateFormat;
    }
}
