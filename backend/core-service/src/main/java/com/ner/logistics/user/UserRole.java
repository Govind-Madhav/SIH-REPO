package com.ner.logistics.user;

import java.util.EnumSet;
import java.util.Set;

public enum UserRole {
    ADMIN(EnumSet.allOf(Permission.class)),

    LOGISTICS_OPERATOR(EnumSet.of(
            Permission.VEHICLE_VIEW,
            Permission.VEHICLE_MANAGE,
            Permission.SHIPMENT_VIEW,
            Permission.SHIPMENT_MANAGE,
            Permission.INCIDENT_VIEW,
            Permission.ROAD_STATUS_VIEW,
            Permission.ROUTE_VIEW,
            Permission.ROUTE_APPROVE,
            Permission.ANALYTICS_VIEW,
            Permission.REPORT_EXPORT
    )),

    EMERGENCY_OPERATOR(EnumSet.of(
            Permission.VEHICLE_VIEW,
            Permission.INCIDENT_VIEW,
            Permission.INCIDENT_VERIFY,
            Permission.INCIDENT_RESOLVE,
            Permission.ROAD_STATUS_VIEW,
            Permission.SOS_VIEW,
            Permission.SOS_ACKNOWLEDGE,
            Permission.SOS_DISPATCH,
            Permission.SOS_RESOLVE,
            Permission.ROUTE_VIEW,
            Permission.ANALYTICS_VIEW
    )),

    FIELD_OFFICER(EnumSet.of(
            Permission.INCIDENT_VIEW,
            Permission.INCIDENT_REPORT,
            Permission.INCIDENT_VERIFY,
            Permission.ROAD_STATUS_VIEW,
            Permission.ROAD_STATUS_UPDATE,
            Permission.ROUTE_VIEW,
            Permission.SOS_VIEW
    )),

    DRIVER(EnumSet.of(
            Permission.VEHICLE_VIEW_SELF,
            Permission.SHIPMENT_VIEW_SELF,
            Permission.ROUTE_VIEW,
            Permission.SOS_TRIGGER,
            Permission.SOS_VIEW_SELF,
            Permission.DELIVERY_STATUS_UPDATE
    ));

    private final Set<Permission> permissions;

    UserRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
