package org.mifos.integrationtest.common.dto.mojaloop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Limit {

    String type;
    Long value;
}
