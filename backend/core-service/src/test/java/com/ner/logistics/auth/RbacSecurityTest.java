package com.ner.logistics.auth;

import com.ner.logistics.user.Permission;
import com.ner.logistics.user.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RbacSecurityTest {

    @Test
    void testAdminPermissionsCompleteSet() {
        UserRole role = UserRole.ADMIN;
        assertTrue(role.getPermissions().contains(Permission.USER_MANAGE));
        assertTrue(role.getPermissions().contains(Permission.ROLE_MANAGE));
        assertTrue(role.getPermissions().contains(Permission.SYSTEM_CONFIG_MANAGE));
        assertTrue(role.getPermissions().contains(Permission.SOS_RESOLVE));
    }

    @Test
    void testLogisticsOperatorPermissions() {
        UserRole role = UserRole.LOGISTICS_OPERATOR;
        assertTrue(role.getPermissions().contains(Permission.VEHICLE_VIEW));
        assertTrue(role.getPermissions().contains(Permission.SHIPMENT_MANAGE));
        assertTrue(role.getPermissions().contains(Permission.ROUTE_APPROVE));

        // Logistics Operator must NOT manage users or system config
        assertFalse(role.getPermissions().contains(Permission.USER_MANAGE));
        assertFalse(role.getPermissions().contains(Permission.SYSTEM_CONFIG_MANAGE));
    }

    @Test
    void testEmergencyOperatorPermissions() {
        UserRole role = UserRole.EMERGENCY_OPERATOR;
        assertTrue(role.getPermissions().contains(Permission.SOS_ACKNOWLEDGE));
        assertTrue(role.getPermissions().contains(Permission.SOS_DISPATCH));
        assertTrue(role.getPermissions().contains(Permission.SOS_RESOLVE));
        assertTrue(role.getPermissions().contains(Permission.INCIDENT_RESOLVE));

        // Emergency Operator must NOT manage users
        assertFalse(role.getPermissions().contains(Permission.USER_MANAGE));
    }

    @Test
    void testFieldOfficerPermissions() {
        UserRole role = UserRole.FIELD_OFFICER;
        assertTrue(role.getPermissions().contains(Permission.INCIDENT_REPORT));
        assertTrue(role.getPermissions().contains(Permission.INCIDENT_VERIFY));
        assertTrue(role.getPermissions().contains(Permission.ROAD_STATUS_UPDATE));

        // Field Officer must NOT approve strategic routes or dispatch SOS
        assertFalse(role.getPermissions().contains(Permission.ROUTE_APPROVE));
        assertFalse(role.getPermissions().contains(Permission.SOS_DISPATCH));
    }

    @Test
    void testDriverPermissions() {
        UserRole role = UserRole.DRIVER;
        assertTrue(role.getPermissions().contains(Permission.VEHICLE_VIEW_SELF));
        assertTrue(role.getPermissions().contains(Permission.SHIPMENT_VIEW_SELF));
        assertTrue(role.getPermissions().contains(Permission.SOS_TRIGGER));

        // Driver must NOT access admin endpoints, verify incidents, or resolve SOS
        assertFalse(role.getPermissions().contains(Permission.USER_MANAGE));
        assertFalse(role.getPermissions().contains(Permission.INCIDENT_VERIFY));
        assertFalse(role.getPermissions().contains(Permission.SOS_RESOLVE));
    }
}
