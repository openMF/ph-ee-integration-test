package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.core.type.TypeReference;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.KeycloakTokenResponse;
import org.mifos.integrationtest.common.dto.kong.Access;
import org.mifos.integrationtest.common.dto.kong.KeycloakUpdateRequest;
import org.mifos.integrationtest.common.dto.kong.KeycloakUser;
import org.mifos.integrationtest.config.KeycloakConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.mifos.integrationtest.common.Utils.CONTENT_TYPE;

public class KeycloakStepDef extends BaseStepDef {

    @Autowired
    KeycloakConfig keycloakConfig;

    @Before("@keycloak-user-setup")
    public void keycloakUserSetup() throws JsonProcessingException {
        logger.info("Running keycloak-user-setup");
        String username = UUID.randomUUID().toString();
        doAdminAuthentication();
        createUser(username);
        BaseStepDef.keycloakUser = fetchKeycloakUserUsingUsername(username);
        logger.debug("Keycloak user: {}", objectMapper.writeValueAsString(BaseStepDef.keycloakUser));
        BaseStepDef.keycloakCurrentUserPassword = "password";
        resetUserPassword(BaseStepDef.keycloakUser.getId(), BaseStepDef.keycloakCurrentUserPassword);
    }

    @After("@keycloak-user-teardown")
    public void keycloakUserTeardown() {
        logger.info("Running keycloak-user-teardown");
        doAdminAuthentication();
        deleteUser(BaseStepDef.keycloakUser.getId());
    }

    @And("I authenticate with new keycloak user")
    public void authenticateCurrentKeycloakUser() throws JsonProcessingException {
        if (BaseStepDef.keycloakUser == null || BaseStepDef.keycloakCurrentUserPassword == null) {
            throw new RuntimeException("Current keycloak user or password is not present." +
                    "Make sure to call create the new user using admin step");
        }
        getTokenFromKeycloakUser(BaseStepDef.keycloakUser.username, BaseStepDef.keycloakCurrentUserPassword);
    }

    @When("I call the keycloak auth api with {string} username and {string} password")
    public void getTokenFromKeycloakUser(String username, String password) {
        RequestSpecification requestSpecification = Utils.getDefaultSpec();
        requestSpecification.header(CONTENT_TYPE, "application/x-www-form-urlencoded");
        requestSpecification
                .formParam(KeycloakConfig.headerUsernameKey, username)
                .formParam(KeycloakConfig.headerPasswordKey, password)
                .formParam(KeycloakConfig.headerClientIdKey, keycloakConfig.clientId)
                .formParam(KeycloakConfig.headerClientSecretKey, keycloakConfig.clientSecret)
                .formParam(KeycloakConfig.headerGrantTypeKey, keycloakConfig.grantType);
        BaseStepDef.response = RestAssured.given(requestSpecification)
                .baseUri(keycloakConfig.keycloakContactPoint)
                .expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(keycloakConfig.tokenEndpoint, keycloakConfig.realm)
                .andReturn().asString();
        try {
            BaseStepDef.keycloakTokenResponse = objectMapper.readValue(BaseStepDef.response, KeycloakTokenResponse.class);
        } catch (Exception e) {
            BaseStepDef.keycloakTokenResponse = null;
        }
        logger.debug("Auth response {}", BaseStepDef.response);
        assertThat(BaseStepDef.keycloakTokenResponse).isNotNull();
        assertThat(BaseStepDef.keycloakTokenResponse.getAccessToken()).isNotNull();
    }

    public void doAdminAuthentication() {
        logger.info("Doing admin auth");
        getTokenFromKeycloakUser(keycloakConfig.adminUsername, keycloakConfig.adminPassword);
    }

    public void deleteUser(String userId) {
        RequestSpecification requestSpecification = Utils.getDefaultSpec();
        requestSpecification.header(CONTENT_TYPE, "application/json");
        if (BaseStepDef.keycloakTokenResponse != null) {
            requestSpecification.header("Authorization", "Bearer " +
                    keycloakTokenResponse.getAccessToken());
        }

        BaseStepDef.response = RestAssured.given(requestSpecification)
                .baseUri(keycloakConfig.keycloakContactPoint)
                .body(keycloakUser)
                .expect().spec(new ResponseSpecBuilder().expectStatusCode(204).build())
                .when()
                .delete(keycloakConfig.userEndpoint+"/{userId}", keycloakConfig.realm, userId)
                .andReturn().asString();
    }

    public void createUser(String username) {
        RequestSpecification requestSpecification = Utils.getDefaultSpec();
        requestSpecification.header(CONTENT_TYPE, "application/json");
        if (BaseStepDef.keycloakTokenResponse != null) {
            requestSpecification.header("Authorization", "Bearer " +
                    keycloakTokenResponse.getAccessToken());
        }

        KeycloakUser keycloakUser = getDefaultKeycloakUser();
        keycloakUser.setUsername(username);

        BaseStepDef.response = RestAssured.given(requestSpecification)
                .baseUri(keycloakConfig.keycloakContactPoint)
                .body(keycloakUser)
                .expect().spec(new ResponseSpecBuilder().expectStatusCode(201).build())
                .when()
                .post(keycloakConfig.userEndpoint, keycloakConfig.realm)
                .andReturn().asString();
    }

    public KeycloakUser fetchKeycloakUserUsingUsername(String username) {
        RequestSpecification requestSpecification = Utils.getDefaultSpec();
        requestSpecification.header(CONTENT_TYPE, "application/json");
        if (BaseStepDef.keycloakTokenResponse != null) {
            requestSpecification.header("Authorization", "Bearer " +
                    keycloakTokenResponse.getAccessToken());
        }

        KeycloakUser keycloakUser = getDefaultKeycloakUser();
        keycloakUser.setUsername(username);

        BaseStepDef.response = RestAssured.given(requestSpecification)
                .baseUri(keycloakConfig.keycloakContactPoint)
                .queryParam("search", username)
                .expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .get(keycloakConfig.userEndpoint, keycloakConfig.realm)
                .andReturn().asString();

        List<KeycloakUser> parsedUsers = null;
        try {
            parsedUsers = objectMapper.readValue(BaseStepDef.response, new TypeReference<List<KeycloakUser>>(){});
            if (parsedUsers.size() == 1) {
                return parsedUsers.get(0);
            }
            return null;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public void resetUserPassword(String userId, String password) {
        RequestSpecification requestSpecification = Utils.getDefaultSpec();
        requestSpecification.header(CONTENT_TYPE, "application/json");
        if (BaseStepDef.keycloakTokenResponse != null) {
            requestSpecification.header("Authorization", "Bearer " +
                    keycloakTokenResponse.getAccessToken());
        }
        KeycloakUpdateRequest keycloakUpdateRequest = getDefaultKeycloakResetPasswordObject();
        keycloakUpdateRequest.setValue(password);

        BaseStepDef.response = RestAssured.given(requestSpecification)
                .baseUri(keycloakConfig.keycloakContactPoint)
                .body(keycloakUpdateRequest)
                .expect().spec(new ResponseSpecBuilder().expectStatusCode(204).build())
                .when()
                .put(keycloakConfig.userPasswordResetEndpoint, keycloakConfig.realm, userId)
                .andReturn().asString();
    }

    public static KeycloakUser getDefaultKeycloakUser() {
        KeycloakUser keycloakUser = new KeycloakUser();
        keycloakUser.setEnabled(true);
        keycloakUser.setEmailVerified(true);
        keycloakUser.setFirstName("Test First Name");
        keycloakUser.setLastName("Test Last Name");
		keycloakUser.setAccess(getDefaultKeycloakAccessObject());
        keycloakUser.addRealmRoles("admin");
        return keycloakUser;
    }

    public static Access getDefaultKeycloakAccessObject() {
        Access access = new Access();
        access.setManageGroupMembership(true);
        access.setView(true);
        access.setMapRoles(true);
        access.setImpersonate(true);
        access.setManage(true);
        return access;
    }

    public static KeycloakUpdateRequest getDefaultKeycloakResetPasswordObject() {
        KeycloakUpdateRequest keycloakUpdateRequest = new KeycloakUpdateRequest();
        keycloakUpdateRequest.setType("password");
        keycloakUpdateRequest.setValue("password");
        keycloakUpdateRequest.setTemporary(false);
        return keycloakUpdateRequest;
    }

}
