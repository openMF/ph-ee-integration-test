package org.mifos.integrationtest.common.dto.operationsapp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sort{
    private String direction;
    private String property;
    private boolean ignoreCase;
    private String nullHandling;
    private boolean descending;
    private boolean ascending;
}
