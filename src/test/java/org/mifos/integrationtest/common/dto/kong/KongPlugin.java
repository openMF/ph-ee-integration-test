package org.mifos.integrationtest.common.dto.kong;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KongPlugin {

    private String id;

    private String name;

    private Map<String, Object> config;

    private boolean enabled;

}
