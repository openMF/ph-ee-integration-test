package org.mifos.integrationtest.common.dto.kong;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KongService {

    private String id;

    private String url;

    private String name;

}
