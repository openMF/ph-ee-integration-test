package org.mifos.integrationtest.common.dto.mojaloop;

import lombok.Builder;import lombok.Getter;

@Getter
@Builder
public class RecordFundsRequestBody {

    String transferId;

    String externalReference;

    String action;

    String reason;

    Amount amount;
}
