package org.mifos.integrationtest.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillStatusReqDTO {

    private String rtpId;
    private String requestId;

    public BillStatusReqDTO(String rtpId, String requestId) {}
}
