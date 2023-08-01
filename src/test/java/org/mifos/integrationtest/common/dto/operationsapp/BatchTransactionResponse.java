package org.mifos.integrationtest.common.dto.operationsapp;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchTransactionResponse {

    @JsonProperty("PollingPath")
    private String pollingPath;

    @JsonProperty("SuggestedCallbackSeconds")
    private String suggestedCallbackSeconds;
}
