package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakConfig {

    @Value("${keycloak.contactpoint}")
    public String keycloakContactPoint;

    @Value("${keycloak.endpoint.token}")
    public String tokenEndpoint;

    @Value("${keycloak.config.client.id}")
    public String clientId;

    @Value("${keycloak.config.client.secret}")
    public String clientSecret;

    @Value("${keycloak.config.grant_type}")
    public String grantType;

    public static String headerUsernameKey = "username";
    public static String headerPasswordKey = "password";
    public static String headerClientIdKey = "client_id";
    public static String headerClientSecretKey = "client_secret";
    public static String headerGrantTypeKey = "grant_type";

}

