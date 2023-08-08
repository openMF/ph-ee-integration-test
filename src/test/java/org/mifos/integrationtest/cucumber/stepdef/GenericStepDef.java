package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.UUID;
import static com.google.common.truth.Truth.assertThat;

/**
 * This class contains the generic step def which is
 * or can be common for multiple test cases. <br>
 * It's very important to not write any payment schema specific code here.
 *
 * @author danishjamal
 */
public class GenericStepDef extends BaseStepDef {

    @And("I have tenant as {string}")
    public void setTenantAnd(String tenant) {
        setTenant(tenant);
    }

    @Then("I should get non empty response")
    public void nonEmptyResponseCheck() {
        assertNonEmpty(BaseStepDef.response);
    }

    private void setTenant(String tenant) {
        BaseStepDef.tenant = tenant;
        assertThat(BaseStepDef.tenant).isNotEmpty();
        BaseStepDef.clientCorrelationId = UUID.randomUUID().toString();
    }

    private void assertNonEmpty(String data) {
        assertThat(data).isNotNull();
    }

}
