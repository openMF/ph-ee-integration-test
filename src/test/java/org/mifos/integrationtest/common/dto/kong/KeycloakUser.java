package org.mifos.integrationtest.common.dto.kong;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeycloakUser {

    public String id;
    public String username;
    public String firstName;
    public String lastName;
    public boolean enabled;
    public boolean emailVerified;
    public Access access;
    public ArrayList<String> realmRoles;

    public void addRealmRoles(String role) {
        if (realmRoles == null) {
            realmRoles = new ArrayList<>();
        }
        realmRoles.add(role);
    }

}
