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
            Permission.DELIVERY_CONFIRM,
            Permission.DELIVERY_PROOF_VIEW,
            Permission.SUPPLY_GAP_VIEW,
            Permission.INCIDENT_VIEW,
            Permission.ROAD_STATUS_VIEW,
            Permission.RISK_CORRIDOR_VIEW,
            Permission.ROUTE_VIEW,
            Permission.ROUTE_APPROVE,
            Permission.DECISION_VIEW,
            Permission.DECISION_APPROVE,
            Permission.ALERT_VIEW,
            Permission.ALERT_ACKNOWLEDGE,
            Permission.ANALYTICS_VIEW,
            Permission.REPORT_EXPORT,
            Permission.INTEGRATION_HEALTH_VIEW
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
            Permission.EMERGENCY_RESOURCE_MANAGE,
            Permission.ROUTE_VIEW,
            Permission.ALERT_VIEW,
            Permission.ALERT_ACKNOWLEDGE,
            Permission.ANALYTICS_VIEW
    )),

    FIELD_OFFICER(EnumSet.of(
            Permission.INCIDENT_VIEW,
            Permission.INCIDENT_REPORT,
            Permission.INCIDENT_VERIFY,
            Permission.ROAD_STATUS_VIEW,
            Permission.ROAD_STATUS_UPDATE,
            Permission.ROUTE_VIEW,
            Permission.SOS_VIEW,
            Permission.ALERT_VIEW
    )),

    DRIVER(EnumSet.of(
            Permission.VEHICLE_VIEW_SELF,
            Permission.SHIPMENT_VIEW_SELF,
            Permission.ROUTE_VIEW,
            Permission.SOS_TRIGGER,
            Permission.SOS_VIEW_SELF,
            Permission.DELIVERY_STATUS_UPDATE,
            Permission.ALERT_VIEW
    ));

    private final Set<Permission> permissions;

    UserRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
