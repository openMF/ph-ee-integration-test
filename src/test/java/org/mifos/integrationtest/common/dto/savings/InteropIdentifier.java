package org.mifos.integrationtest.common.dto.savings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Setter
@Getter
public class InteropIdentifier {
    public String accountId;
    public InteropIdentifier(String externalId){
        this.accountId = externalId;
    }
}
