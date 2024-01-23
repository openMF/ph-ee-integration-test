package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.kong.KongConsumer;
import org.mifos.integrationtest.common.dto.kong.KongConsumerKey;
import org.mifos.integrationtest.common.dto.kong.KongPlugin;
import org.mifos.integrationtest.common.dto.kong.KongRoute;
import org.mifos.integrationtest.common.dto.kong.KongService;
import org.mifos.integrationtest.config.KeycloakConfig;
import org.mifos.integrationtest.config.KongConfig;
import org.mifos.integrationtest.config.KongOidcPluginConfig;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("AvoidDoubleBraceInitialization")
public class KongStepDef extends BaseStepDef {

    @Autowired
    public KongConfig kongConfig;

    @Autowired
    public KeycloakConfig keycloakConfig;

    @Autowired
    public KongOidcPluginConfig kongOidcPluginConfig;

    @Given("I have required Kong configuration")
    public void checkKongConfigNotNull() {
        assertThat(this.kongConfig).isNotNull();
    }

    @When("I create new consumer")
    public void createNewConsumer() throws JsonProcessingException {
        KongConsumer consumer = new KongConsumer();
        consumer.setCustomId("custom_" + System.currentTimeMillis());
        consumer.setId(UUID.randomUUID().toString());
        consumer.setUsername("user_" + System.currentTimeMillis());

        RequestSpecification baseReqSpec = Utils.getDefaultSpec();

        scenarioScopeDef.response = RestAssured.given(baseReqSpec).baseUri(kongConfig.adminContactPoint)
                .header("Content-Type", "application/json").body(objectMapper.writeValueAsString(consumer)).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(201).build()).when().post(kongConfig.consumerEndpoint).andReturn()
                .asString();

        logger.debug("Create new consumer response from kong: {}", scenarioScopeDef.response);
        try {
            scenarioScopeDef.kongConsumer = objectMapper.readValue(scenarioScopeDef.response, KongConsumer.class);
            logger.debug("Kong consumer: {}", objectMapper.writeValueAsString(scenarioScopeDef.kongConsumer));
        } catch (Exception e) {
            scenarioScopeDef.kongConsumer = null;
        }

        assertThat(scenarioScopeDef.kongConsumer).isNotNull();
    }

    @And("I am able to create a key for above consumer")
    public void createKey() throws JsonProcessingException {
        KongConsumerKey consumerKey = new KongConsumerKey();
        consumerKey.setKey(UUID.randomUUID().toString());
        consumerKey.setId(UUID.randomUUID().toString());

        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        scenarioScopeDef.response = RestAssured.given(baseReqSpec).baseUri(kongConfig.adminContactPoint)
                .header("Content-Type", "application/json").body(objectMapper.writeValueAsString(consumerKey)).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(201).build()).when()
                .post(kongConfig.createKeyEndpoint, scenarioScopeDef.kongConsumer.getUsername()).andReturn().asString();

        logger.debug("Create new key response from kong: {}", scenarioScopeDef.response);
        try {
            scenarioScopeDef.kongConsumerKey = objectMapper.readValue(scenarioScopeDef.response, KongConsumerKey.class);
            logger.debug("Kong consumer key: {}", objectMapper.writeValueAsString(scenarioScopeDef.kongConsumerKey));
        } catch (Exception e) {
            scenarioScopeDef.kongConsumerKey = null;
        }

        assertThat(scenarioScopeDef.kongConsumerKey).isNotNull();
    }

    @And("I register a service in kong")
    public void registerService() throws JsonProcessingException {
        registerService(kongConfig.serviceUrl, "https");
        assertThat(scenarioScopeDef.kongService).isNotNull();
    }

    @And("I register a route to above service in kong")
    public void registerRouteInService() throws JsonProcessingException {
        registerRouteInService(new ArrayList<>() {

            {
                add("/");
            }
        }, new ArrayList<>() {

            {
                add(kongConfig.routeHost);
            }
        }, scenarioScopeDef.kongService.getId());
        assertThat(scenarioScopeDef.kongRoute).isNotNull();
    }

    @And("I add the key-auth plugin in above service")
    public void enableKeyAuthPlugin() throws JsonProcessingException {
        Map<String, Object> config = new HashMap<>() {

            {
                put("key_names", new ArrayList<String>() {

                    {
                        add(kongConfig.apiKeyHeader);
                    }
                });
            }
        };
        enablePluginForService(scenarioScopeDef.kongService.getId(), "key-auth", config);
        assertThat(scenarioScopeDef.kongPlugin).isNotNull();
    }

    @Then("When I call the service endpoint with api key I should get {int}")
    public void callServiceEndpoint(int expectedStatus) {
        logger.debug("Key: {}", scenarioScopeDef.kongConsumerKey.getKey());
        logger.debug("Host: {}", kongConfig.routeHost);
        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        try {
            Response resp = RestAssured.given(baseReqSpec).baseUri("https://" + kongConfig.routeHost)
                    .header(kongConfig.apiKeyHeader, scenarioScopeDef.kongConsumerKey.getKey()).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when().get("/actuator/health/liveness")
                    .andReturn();
            scenarioScopeDef.response = resp.asString();

            logger.debug("Status Code: {}", resp.getStatusCode());
            logger.debug("Response: {}", scenarioScopeDef.response);
        } catch (Exception e) {
            logger.debug("{}", e.getMessage());
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

    @And("I register channel service using config")
    public void registerChannelServiceFromConfig() throws JsonProcessingException {
        registerService(kongConfig.channelServiceUrl, "https");
    }

    @And("I register service with url {string} and {string} protocol")
    public void commonRegisterService(String serviceUrl, String protocol) throws JsonProcessingException {
        registerService(serviceUrl, protocol);
        assertThat(scenarioScopeDef.kongService).isNotNull();
    }

    @And("I register channel route using config")
    public void registerChannelRouteFromConfig() throws JsonProcessingException {
        commonRegisterRouteInService(kongConfig.channelRouteHost, kongConfig.channelRoutePath);
    }

    @And("I register route with route host {string} and path {string}")
    public void commonRegisterRouteInService(String routeHost, String path) throws JsonProcessingException {
        registerRouteInService(new ArrayList<>() {

            {
                add(path);
            }
        }, new ArrayList<>() {

            {
                add(routeHost);
            }
        }, scenarioScopeDef.kongService.getId());
        assertThat(scenarioScopeDef.kongRoute).isNotNull();
    }

    @And("I enable oidc plugin")
    public void enableOidcPluginForService() throws JsonProcessingException {
        Map<String, Object> config = new HashMap<>() {

            {
                put("discovery", keycloakConfig.discoveryUrl.replace("{realm}", keycloakConfig.realm));
                put("client_id", keycloakConfig.clientId);
                put("client_secret", keycloakConfig.clientSecret);
                put("introspection_endpoint", keycloakConfig.introspectionUrl.replace("{realm}", keycloakConfig.realm));
                put("bearer_only", kongOidcPluginConfig.bearerTokenOnly ? "yes" : "no");
                put("scope", kongOidcPluginConfig.scope);
                put("realm", keycloakConfig.realm);
            }
        };
        enablePluginForService(scenarioScopeDef.kongService.getId(), "oidc", config);
        assertThat(scenarioScopeDef.kongPlugin).isNotNull();
    }

    public void registerService(String serviceUrl, String protocol) throws JsonProcessingException {
        KongService service = new KongService();
        service.setId(UUID.randomUUID().toString());
        service.setUrl(serviceUrl);
        service.setName("name_" + service.getId());
        service.setProtocol(protocol);

        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        scenarioScopeDef.response = RestAssured.given(baseReqSpec).baseUri(kongConfig.adminContactPoint)
                .header("Content-Type", "application/json").body(objectMapper.writeValueAsString(service)).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(201).build()).when().post(kongConfig.servicesEndpoint).andReturn()
                .asString();

        logger.debug("Create new service response from kong: {}", scenarioScopeDef.response);
        try {
            scenarioScopeDef.kongService = objectMapper.readValue(scenarioScopeDef.response, KongService.class);
            logger.debug("Kong service: {}", objectMapper.writeValueAsString(scenarioScopeDef.kongService));
        } catch (Exception e) {
            scenarioScopeDef.kongService = null;
        }
    }

    public void registerRouteInService(ArrayList<String> paths, ArrayList<String> routeHosts, String serviceId)
            throws JsonProcessingException {
        KongRoute route = new KongRoute();
        route.setId(UUID.randomUUID().toString());
        route.setName("name_" + route.getId());
        route.setPaths(paths);
        route.setHosts(routeHosts);

        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        scenarioScopeDef.response = RestAssured.given(baseReqSpec).baseUri(kongConfig.adminContactPoint)
                .header("Content-Type", "application/json").body(objectMapper.writeValueAsString(route)).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(201).build()).when().post(kongConfig.createRouteEndpoint, serviceId)
                .andReturn().asString();

        logger.debug("Create new route response from kong: {}", scenarioScopeDef.response);
        try {
            scenarioScopeDef.kongRoute = objectMapper.readValue(scenarioScopeDef.response, KongRoute.class);
            logger.debug("Kong route: {}", objectMapper.writeValueAsString(scenarioScopeDef.kongRoute));
        } catch (Exception e) {
            scenarioScopeDef.kongRoute = null;
        }
    }

    public void enablePluginForService(String serviceId, String pluginName, Map<String, Object> config) throws JsonProcessingException {
        KongPlugin kongPlugin = new KongPlugin();
        kongPlugin.setId(UUID.randomUUID().toString());
        kongPlugin.setName(pluginName);
        kongPlugin.setEnabled(true);
        kongPlugin.setConfig(config);

        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        scenarioScopeDef.response = RestAssured.given(baseReqSpec).baseUri(kongConfig.adminContactPoint)
                .header("Content-Type", "application/json").body(objectMapper.writeValueAsString(kongPlugin)).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(201).build()).when().post(kongConfig.createPluginEndpoint, serviceId)
                .andReturn().asString();

        logger.debug("Enable plugin response from kong: {}", scenarioScopeDef.response);
        try {
            scenarioScopeDef.kongPlugin = objectMapper.readValue(scenarioScopeDef.response, KongPlugin.class);
            logger.debug("Kong plugin: {}", objectMapper.writeValueAsString(scenarioScopeDef.kongPlugin));
        } catch (Exception e) {
            scenarioScopeDef.kongPlugin = null;
        }
    }

    // cals respective methods for clearing kong related resources
    private void clearKongData() {
        if (scenarioScopeDef.kongConsumer != null) {
            deleteConsumer(scenarioScopeDef.kongConsumer.getId());
            scenarioScopeDef.kongConsumer = null;
            scenarioScopeDef.kongConsumerKey = null;
        }
        if (scenarioScopeDef.kongPlugin != null && StringUtils.isNotBlank(scenarioScopeDef.kongPlugin.getId())) {
            deletePlugin(scenarioScopeDef.kongPlugin.getId());
            scenarioScopeDef.kongPlugin = null;
        }
        if (scenarioScopeDef.kongRoute != null) {
            deleteRoute(scenarioScopeDef.kongRoute.getId());
            scenarioScopeDef.kongRoute = null;
        }
        if (scenarioScopeDef.kongService != null) {
            deleteService(scenarioScopeDef.kongService.getId());
            scenarioScopeDef.kongService = null;
        }
    }

    // deletes the consumer in kong by calling admin api
    private void deleteConsumer(String consumerId) {
        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        String deleteResponse = RestAssured.given(baseReqSpec).baseUri(kongConfig.adminContactPoint)
                .delete(kongConfig.consumerEndpoint + "/{consumerId}", consumerId).andReturn().asString();
        logger.debug("Consumer delete response: {}", deleteResponse);
    }

    // deletes the plugin in kong by calling admin api
    private void deletePlugin(String pluginId) {
        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        String deleteResponse = RestAssured.given(baseReqSpec).baseUri(kongConfig.adminContactPoint)
                .delete(kongConfig.pluginsEndpoint + "/{pluginId}", pluginId).andReturn().asString();
        logger.debug("Plugin delete response: {}", deleteResponse);
    }

    // deletes the route in kong by calling admin api
    private void deleteRoute(String routeId) {
        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        String deleteResponse = RestAssured.given(baseReqSpec).baseUri(kongConfig.adminContactPoint)
                .delete(kongConfig.routesEndpoint + "/{routeId}", routeId).andReturn().asString();
        logger.debug("Route delete response: {}", deleteResponse);
    }

    // deletes the service in kong by calling admin api
    private void deleteService(String serviceId) {
        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        String deleteResponse = RestAssured.given(baseReqSpec).baseUri(kongConfig.adminContactPoint)
                .delete(kongConfig.servicesEndpoint + "/{serviceId}", serviceId).andReturn().asString();
        logger.debug("Service delete response: {}", deleteResponse);
    }

    @And("I add the ratelimiter plugin in above service")
    public void enableRateLimitPlugin() throws JsonProcessingException {
        KongPlugin kongPlugin = new KongPlugin();
        kongPlugin.setId(UUID.randomUUID().toString());
        kongPlugin.setName("rate-limiting");
        kongPlugin.setEnabled(true);
        kongPlugin.setConfig(new HashMap<>() {

            {
                put("policy", "cluster");
                put("minute", 2);
                put("limit_by", "consumer");
            }
        });

        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        scenarioScopeDef.response = RestAssured.given(baseReqSpec).baseUri(kongConfig.adminContactPoint)
                .header("Content-Type", "application/json").body(objectMapper.writeValueAsString(kongPlugin)).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(201).build()).when()
                .post(kongConfig.createPluginEndpoint, scenarioScopeDef.kongService.getId()).andReturn().asString();

        logger.info("Enable ratelimiter plugin response from kong: {}", scenarioScopeDef.response);
        try {
            scenarioScopeDef.kongPlugin = objectMapper.readValue(scenarioScopeDef.response, KongPlugin.class);
            logger.info("Kong plugin: {}", objectMapper.writeValueAsString(scenarioScopeDef.kongPlugin));
        } catch (Exception e) {
            scenarioScopeDef.kongPlugin = null;
        }

        assertThat(scenarioScopeDef.kongPlugin).isNotNull();
    }

    @And("I add the ratelimiter plugin in kong")
    public void iAddTheRatelimiterPluginInKong() throws JsonProcessingException {
        KongPlugin kongPlugin = new KongPlugin();
        kongPlugin.setId(UUID.randomUUID().toString());
        kongPlugin.setName("rate-limiting");
        kongPlugin.setEnabled(true);
        kongPlugin.setConfig(new HashMap<>() {

            {
                put("policy", "cluster");
                put("second", 1);
                put("minute", 5);
                put("limit_by", "consumer");
            }
        });

        RequestSpecification baseReqSpec = Utils.getDefaultSpec();
        scenarioScopeDef.response = RestAssured.given(baseReqSpec).baseUri(kongConfig.adminContactPoint)
                .header("Content-Type", "application/json").body(objectMapper.writeValueAsString(kongPlugin)).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(201).build()).when().post(kongConfig.pluginsEndpoint).andReturn()
                .asString();

        logger.info("Creating ratelimiter plugin response from kong: {}", scenarioScopeDef.response);
        try {
            scenarioScopeDef.kongPlugin = objectMapper.readValue(scenarioScopeDef.response, KongPlugin.class);
            logger.info("Kong plugin: {}", objectMapper.writeValueAsString(scenarioScopeDef.kongPlugin));
        } catch (Exception e) {
            scenarioScopeDef.kongPlugin = null;
        }

        assertThat(scenarioScopeDef.kongPlugin).isNotNull();
    }

    @And("I should have {string} in response body")
    public void iShouldHaveInResponseBody(String expectedResponse) {
        assertThat(scenarioScopeDef.response).contains(expectedResponse);
    }
}
