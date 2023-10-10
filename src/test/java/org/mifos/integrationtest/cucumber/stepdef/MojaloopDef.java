package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import org.mifos.integrationtest.common.dto.mojaloop.AddUserAlsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MojaloopDef {

    @Autowired
    ObjectMapper objectMapper;

    protected String setBodyAddAlsUser(String fspId)throws JsonProcessingException {
        AddUserAlsRequest addUserAlsRequest = new AddUserAlsRequest();
        addUserAlsRequest.setCurrency("USD");
        addUserAlsRequest.setFspId(fspId);
        return objectMapper.writeValueAsString(addUserAlsRequest);
    }
}
