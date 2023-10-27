package org.mifos.integrationtest.common.dto.kong;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KongKeyAuthPluginConfig {

    @JsonProperty("key_names")
    private ArrayList<String> keyNames;

}
