package org.mifos.integrationtest.common.dto.paybill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaybillResponseDTO {
    String transactionId;
    boolean reconciled;
    String ams;
}
