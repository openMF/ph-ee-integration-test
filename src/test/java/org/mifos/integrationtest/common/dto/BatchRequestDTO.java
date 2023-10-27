package org.mifos.integrationtest.common.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchRequestDTO {

    List<Party> creditParty;
    List<Party> debitParty;

    String subType;
    String amount;
    String currency;
    String descriptionText;

}
