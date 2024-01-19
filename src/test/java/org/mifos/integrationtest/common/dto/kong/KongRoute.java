package org.mifos.integrationtest.common.dto.kong;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KongRoute {

    private String id;

    private String name;

    private ArrayList<String> hosts;

    private ArrayList<String> paths;

}
