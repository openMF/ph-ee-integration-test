package org.mifos.integrationtest.common.dto.operationsapp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mifos.connector.common.operations.dto.Transfer;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BatchDetailResponse {

    List<Transfer> content;
}
