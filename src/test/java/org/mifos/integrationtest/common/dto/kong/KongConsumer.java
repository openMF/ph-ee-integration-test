package org.mifos.integrationtest.common.dto.kong;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KongConsumer {

    private String id;

    private String username;

    @JsonProperty("custom_id")
    private String customId;

}
