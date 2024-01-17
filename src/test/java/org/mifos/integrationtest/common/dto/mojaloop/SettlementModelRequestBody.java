package org.mifos.integrationtest.common.dto.mojaloop;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SettlementModelRequestBody {

    private String name;
    private String settlementGranularity;
    private String settlementInterchange;
    private String settlementDelay;
    private String currency;
    private boolean requireLiquidityCheck;
    private String ledgerAccountType;
    private boolean autoPositionReset;
    private String settlementAccountType;
}
