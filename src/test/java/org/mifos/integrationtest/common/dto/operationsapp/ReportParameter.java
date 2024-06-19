package org.mifos.integrationtest.common.dto.operationsapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ReportParameter {

    private Long id;

    private ReportRequestDTO reportRequest;

    private String parameterKey;

    private String parameterValue;
}
