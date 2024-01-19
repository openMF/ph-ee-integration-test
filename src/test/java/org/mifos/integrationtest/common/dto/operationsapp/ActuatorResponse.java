package org.mifos.integrationtest.common.dto.operationsapp;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActuatorResponse {

    public String status;
    public ArrayList<String> groups;
}
