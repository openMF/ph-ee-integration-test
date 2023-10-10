package org.mifos.integrationtest.common.dto.mojaloop;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddUserAlsRequest {

    String fspId;

    String currency;
}
