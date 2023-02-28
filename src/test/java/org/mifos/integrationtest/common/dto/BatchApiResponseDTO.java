package org.mifos.integrationtest.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class BatchApiResponseDTO {

    //@JsonProperty("batch_id")
    public String batchId;

    //@JsonProperty("request_id")
    public String requestId;

    //@JsonProperty("status")
    public String status;

}
