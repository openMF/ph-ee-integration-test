package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.kong.KongConsumer;
import org.mifos.integrationtest.common.dto.kong.KongConsumerKey;
import org.mifos.integrationtest.common.dto.kong.KongPlugin;
import org.mifos.integrationtest.common.dto.kong.KongRoute;
import org.mifos.integrationtest.common.dto.kong.KongService;
import org.mifos.integrationtest.config.KongConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

public class KongStepDef extends BaseStepDef {

    @Autowired
    public KongConfig kongConfig;

    @Given("I have required Kong configuration")
    public void checkKongConfigNotNull() {
        assertThat(this.kongConfig).isNotNull();
    }

    @When("I create new consumer")
    public void createNewConsumer() throws JsonProcessingException {
        KongConsumer consumer = new KongConsumer();
        consumer.setCustomId("custom_"+System.currentTimeMillis());
        consumer.setId(UUID.randomUUID().toString());
        consumer.setUsername("user_"+System.currentTimeMillis());

        RequestSpecification baseReqSpec = Utils.getDefaultSpec();

        BaseStepDef.response = RestAssured.given(baseReqSpec)
                .baseUri(kongConfig.adminContactPoint)
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(consumer)).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(201).build()).when()
                .post(kongConfig.consumerEndpoint).andReturn().asString();

        logger.debug("Create new consumer response from kong: {}", BaseStepDef.response);
        try {
            BaseStepDef.kongConsumer = objectMapper.readValue(BaseStepDef.response, KongConsumer.class);
            logger.debug("Kong consumer: {}", objectMapper.writeValueAsString(BaseStepDef.kongConsumer));
        } catch (Exception e) {
            BaseStepDef.kongConsumer = null;
        }

        assertThat(BaseStepDef.kongConsumer).isNotNull();
    }

    @And("I am able to create a key for above consumer")
    public void createKey() throws JsonProcessingException {
        KongConsumerKey consumerKey = new KongConsumerKey();
        consumerKey.setKey(UUID.randomUUID().toString());
        consumerKey.setId(UUID.randomUUID().toString());

        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(baseReqSpec)
                .baseUri(kongConfig.adminContactPoint)
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(consumerKey)).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(201).build()).when()
                .post(kongConfig.createKeyEndpoint, BaseStepDef.kongConsumer.getUsername()).andReturn().asString();

        logger.debug("Create new key response from kong: {}", BaseStepDef.response);
        try {
            BaseStepDef.kongConsumerKey = objectMapper.readValue(BaseStepDef.response, KongConsumerKey.class);
            logger.debug("Kong consumer key: {}", objectMapper.writeValueAsString(BaseStepDef.kongConsumerKey));
        } catch (Exception e) {
            BaseStepDef.kongConsumerKey = null;
        }

        assertThat(BaseStepDef.kongConsumerKey).isNotNull();
    }

    @And("I register a service in kong")
    public void registerService() throws JsonProcessingException {
        KongService service = new KongService();
        service.setId(UUID.randomUUID().toString());
        service.setUrl(kongConfig.serviceUrl);
        service.setName("name_"+service.getId());

        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(baseReqSpec)
                .baseUri(kongConfig.adminContactPoint)
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(service)).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(201).build()).when()
                .post(kongConfig.servicesEndpoint).andReturn().asString();

        logger.debug("Create new service response from kong: {}", BaseStepDef.response);
        try {
            BaseStepDef.kongService = objectMapper.readValue(BaseStepDef.response, KongService.class);
            logger.debug("Kong service: {}", objectMapper.writeValueAsString(BaseStepDef.kongService));
        } catch (Exception e) {
            BaseStepDef.kongService = null;
        }

        assertThat(BaseStepDef.kongService).isNotNull();
    }

    @And("I register a route to above service in kong")
    public void registerRouteInService() throws JsonProcessingException {
        KongRoute route = new KongRoute();
        route.setId(UUID.randomUUID().toString());
        route.setName("name_"+route.getId());
        route.setPaths(new ArrayList<>(){{ add("/actuator/health/liveness"); }});
        route.setHosts(new ArrayList<>(){{ add(kongConfig.routeHost); }});

        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(baseReqSpec)
                .baseUri(kongConfig.adminContactPoint)
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(route)).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(201).build()).when()
                .post(kongConfig.createRouteEndpoint, BaseStepDef.kongService.getId()).andReturn().asString();

        logger.debug("Create new route response from kong: {}", BaseStepDef.response);
        try {
            BaseStepDef.kongRoute = objectMapper.readValue(BaseStepDef.response, KongRoute.class);
            logger.debug("Kong route: {}", objectMapper.writeValueAsString(BaseStepDef.kongRoute));
        } catch (Exception e) {
            BaseStepDef.kongRoute = null;
        }

        assertThat(BaseStepDef.kongRoute).isNotNull();
    }

    @And("I add the key-auth plugin in above service")
    public void enableKeyAuthPlugin() throws JsonProcessingException {
        KongPlugin kongPlugin = new KongPlugin();
        kongPlugin.setId(UUID.randomUUID().toString());
        kongPlugin.setName("key-auth");
        kongPlugin.setEnabled(true);
        kongPlugin.setConfig(new HashMap<>(){{
            put("key_names", new ArrayList<String>(){{
                add(kongConfig.apiKeyHeader);
            }});
        }});

        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(baseReqSpec)
                .baseUri(kongConfig.adminContactPoint)
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(kongPlugin)).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(201).build()).when()
                .post(kongConfig.createPluginEndpoint, BaseStepDef.kongService.getId()).andReturn().asString();

        logger.debug("Enable key-auth plugin response from kong: {}", BaseStepDef.response);
        try {
            BaseStepDef.kongPlugin = objectMapper.readValue(BaseStepDef.response, KongPlugin.class);
            logger.debug("Kong plugin: {}", objectMapper.writeValueAsString(BaseStepDef.kongPlugin));
        } catch (Exception e) {
            BaseStepDef.kongPlugin = null;
        }

        assertThat(BaseStepDef.kongPlugin).isNotNull();
    }

    @Then("When I call the service endpoint with api key I should get {int}")
    public void callServiceEndpoint(int expectedStatus) {
        logger.debug("Key: {}", BaseStepDef.kongConsumerKey.getKey());
        logger.debug("Host: {}", kongConfig.routeHost);
        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        try {
            Response resp = RestAssured.given(baseReqSpec)
                    .baseUri("http://"+kongConfig.routeHost)
                    .header(kongConfig.apiKeyHeader, BaseStepDef.kongConsumerKey.getKey())
                    .expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                    .when()
                    .get("/actuator/health/liveness").andReturn();
            BaseStepDef.response = resp.asString();

            logger.debug("Status Code: {}", resp.getStatusCode());
            logger.debug("Response: {}", BaseStepDef.response);
        } catch (Exception e) {
            e.printStackTrace();
            clearKongData();
        }
    }

    @After("@kong-teardown")
    public void removeKeyAuth() {
        logger.debug("Running kong teardown");
        clearKongData();
    }

    @And("I wait for {int} seconds")
    public void waitForSometime(int seconds) {
        Utils.sleep(seconds);
    }

    // cals respective methods for clearing kong related resources
    private void clearKongData() {
        if (BaseStepDef.kongConsumer != null) {
            deleteConsumer(BaseStepDef.kongConsumer.getId());
            BaseStepDef.kongConsumer = null;
            BaseStepDef.kongConsumerKey = null;
        }
        if (BaseStepDef.kongPlugin != null) {
            deletePlugin(BaseStepDef.kongPlugin.getId());
            BaseStepDef.kongPlugin= null;
        }
        if (BaseStepDef.kongRoute != null) {
            deleteRoute(BaseStepDef.kongRoute.getId());
            BaseStepDef.kongRoute = null;
        }
        if (BaseStepDef.kongService != null) {
            deleteService(BaseStepDef.kongService.getId());
            BaseStepDef.kongService = null;
        }
    }

    // deletes the consumer in kong by calling admin api
    private void deleteConsumer(String consumerId) {
        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        String deleteResponse = RestAssured.given(baseReqSpec)
                .baseUri(kongConfig.adminContactPoint)
                .delete(kongConfig.consumerEndpoint+"/{consumerId}",consumerId)
                .andReturn().asString();
        logger.debug("Consumer delete response: {}", deleteResponse);
    }

    // deletes the plugin in kong by calling admin api
    private void deletePlugin(String pluginId) {
        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        String deleteResponse = RestAssured.given(baseReqSpec)
                .baseUri(kongConfig.adminContactPoint)
                .delete(kongConfig.pluginsEndpoint+"/{pluginId}",pluginId)
                .andReturn().asString();
        logger.debug("Plugin delete response: {}", deleteResponse);
    }

    // deletes the route in kong by calling admin api
    private void deleteRoute(String routeId) {
        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        String deleteResponse = RestAssured.given(baseReqSpec)
                .baseUri(kongConfig.adminContactPoint)
                .delete(kongConfig.routesEndpoint+"/{routeId}",routeId)
                .andReturn().asString();
        logger.debug("Route delete response: {}", deleteResponse);
    }

    // deletes the service in kong by calling admin api
    private void deleteService(String serviceId) {
        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        String deleteResponse = RestAssured.given(baseReqSpec)
                .baseUri(kongConfig.adminContactPoint)
                .delete(kongConfig.servicesEndpoint+"/{serviceId}",serviceId)
                .andReturn().asString();
        logger.debug("Service delete response: {}", deleteResponse);
    }

}
