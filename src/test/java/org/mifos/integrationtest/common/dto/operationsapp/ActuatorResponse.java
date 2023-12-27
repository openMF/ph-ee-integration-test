package org.mifos.integrationtest.common.dto.operationsapp;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ActuatorResponse {
    public String status;
    public ArrayList<String> groups;
}
