package org.mifos.integrationtest.common.dto.mojaloop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddUserAlsRequest {

    String fspId;

    String currency;
}
