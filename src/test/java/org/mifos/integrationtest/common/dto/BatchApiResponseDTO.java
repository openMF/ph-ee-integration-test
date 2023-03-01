package org.mifos.integrationtest.common.dto;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchApiResponseDTO {

    @JsonProperty("batch_id")
    private String batchId;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("status")
    private String status;

}
