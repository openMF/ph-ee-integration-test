package org.mifos.integrationtest.common.dto.operationsapp;

import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDTO {

    private Long id;

    private String reportName;

    private String reportType;

    private String reportSubType;

    private String reportCategory;

    private String description;

    private String reportSql;

    private List<ReportParameter> reportParameters;
}
