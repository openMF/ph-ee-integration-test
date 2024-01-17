package org.mifos.integrationtest.common.dto.mojaloop;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HubAccountSetupRequestBody {

    String type;

    String currency;
}
