package org.mifos.integrationtest.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchRequestDTO {

    List<Party> creditParty, debitParty;

    String paymentMode, amount, currency, descriptionText;

}
