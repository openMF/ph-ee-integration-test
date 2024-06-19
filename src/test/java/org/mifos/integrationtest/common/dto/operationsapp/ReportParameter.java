package org.mifos.integrationtest.common.dto.operationsapp;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ReportParameter {

    private Long id;

    private ReportRequestDTO reportRequest;

    private String parameterKey;

    private String parameterValue;
}
