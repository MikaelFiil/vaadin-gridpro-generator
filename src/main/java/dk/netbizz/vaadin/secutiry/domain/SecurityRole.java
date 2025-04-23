package dk.netbizz.vaadin.secutiry.domain;

import lombok.Getter;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum SecurityRole {
    // This Role is NEVER visible in the application, it can ONLY be inserted by a DIRECT DB SQL INSERT
    @FieldNameConstants.Include Administrator("Administrator"),         // never visible
    @FieldNameConstants.Include System("System"),                       // never visible
    // Superuser role is only visible for selection when an Administrator has logged in and is creating Users using the Control Panel
    @FieldNameConstants.Include Superuser("Superuser"),                 //  Only given to specific Users by Administrators

    // THe Roles below are visible to Admin
    @FieldNameConstants.Include User("User");
    private final String label;

    SecurityRole(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static SecurityRole valueOfLabel(String label) {
        for (SecurityRole e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }

    public static List<SecurityRole> getAllCustomerRoles() {
        List<SecurityRole> securityRoleListNoAdmin = new ArrayList<SecurityRole>(Arrays.asList(SecurityRole.values()));
        securityRoleListNoAdmin.remove(SecurityRole.Administrator);
        securityRoleListNoAdmin.remove(SecurityRole.System);
        securityRoleListNoAdmin.remove(SecurityRole.Superuser);
        return securityRoleListNoAdmin;
    }


}
