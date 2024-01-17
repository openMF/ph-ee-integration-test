package org.mifos.integrationtest.common.dto.mojaloop;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Amount {

    Long amount;

    String currency;
}
