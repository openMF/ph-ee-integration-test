package org.mifos.integrationtest.common.dto.kong;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

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
