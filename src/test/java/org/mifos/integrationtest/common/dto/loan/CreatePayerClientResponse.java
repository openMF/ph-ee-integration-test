package org.mifos.integrationtest.common.dto.loan;

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
public class CreatePayerClientResponse {
    public String officeId;
    public String clientId;
    public String resourceId;
}
