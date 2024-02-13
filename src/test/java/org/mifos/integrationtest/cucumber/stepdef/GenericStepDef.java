package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.util.UUID;
import org.mifos.integrationtest.config.TenantConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * This class contains the generic step def which is or can be common for multiple test cases. <br>
 * It's very important to not write any payment schema specific code here.
 *
 * @author danishjamal
 */
public class GenericStepDef extends BaseStepDef {

    @Autowired
    TenantConfig tenantConfig;

    @Value("${global_wait_time_ms}")
    private int globalWaitTime;

    @And("I have tenant as {string}")
    public void tenantAnd(String tenant) {
        scenarioScopeState.tenant = tenantConfig.getTenant(tenant.toLowerCase());
    }

    @Then("I should get non empty response")
    public void nonEmptyResponseCheck() {
        assertNonEmpty(scenarioScopeState.response);
    }

    @And("I will sleep for {int} millisecond")
    public void iWillSleepForSecs(int time) throws InterruptedException {
        Thread.sleep(time + globalWaitTime);
    }

    @And("I store this time as start time")
    public void storeCurrentTime() {
        scenarioScopeState.dateTime = getCurrentDateInFormat();
    }

    private void setTenant(String tenant) {
        scenarioScopeState.tenant = tenant;
        assertThat(scenarioScopeState.tenant).isNotEmpty();
        scenarioScopeState.clientCorrelationId = UUID.randomUUID().toString();
    }

    private void assertNonEmpty(String data) {
        assertThat(data).isNotNull();
    }

}
