package org.mifos.integrationtest.common.dto.mojaloop;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OracleOnboardRequestBody {

    String oracleIdType;
    Endpoint endpoint;
    String currency;
    Boolean isDefault;
}
