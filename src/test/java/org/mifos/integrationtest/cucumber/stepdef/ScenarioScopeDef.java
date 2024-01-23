package org.mifos.integrationtest.cucumber.stepdef;

import org.springframework.stereotype.Component;

@Component
public class ScenarioScopeDef {

    protected String payerIdentifier;
    protected String payeeIdentifier;

    protected String batchId;
    protected String tenant;
    protected String response;
    protected String request;
    protected Integer statusCode;
    protected String accessToken;
    protected String filename;

}
