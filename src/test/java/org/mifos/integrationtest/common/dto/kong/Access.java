package org.mifos.integrationtest.common.dto.kong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Access {

    public boolean manageGroupMembership, view, mapRoles, impersonate, manage;
}
