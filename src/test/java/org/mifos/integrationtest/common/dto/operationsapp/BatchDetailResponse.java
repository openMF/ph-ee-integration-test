package org.mifos.integrationtest.common.dto.operationsapp;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mifos.connector.common.operations.dto.Transfer;

@Getter
@Setter
@NoArgsConstructor
public class BatchDetailResponse {

    List<Transfer> content;
}
