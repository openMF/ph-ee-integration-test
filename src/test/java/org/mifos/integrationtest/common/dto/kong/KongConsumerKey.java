package org.mifos.integrationtest.common.dto.kong;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KongConsumerKey {

    private String id;

    private String key;
}
