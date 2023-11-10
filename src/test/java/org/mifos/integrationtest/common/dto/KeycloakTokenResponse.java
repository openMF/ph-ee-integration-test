package org.mifos.integrationtest.common.dto;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakTokenResponse {

    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("refresh_token")
    String refreshToken;

    @JsonProperty("token_type")
    String tokenType;

    @JsonProperty("session_state")
    String sessionState;

    @JsonProperty("scope")
    String scope;

    @JsonProperty("expires_in")
    int expiresIn;

    @JsonProperty("refresh_expires_in")
    int refreshTokenExpiresIn;

    @JsonProperty("not-before-policy")
    int notBeforePolicy;

}
