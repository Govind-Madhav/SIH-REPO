package com.ner.logistics.user;

public enum Permission {
    // Governance & System Admin
    USER_MANAGE,
    ROLE_MANAGE,
    DEVICE_MANAGE,
    SYSTEM_CONFIG_MANAGE,
    AUDIT_LOG_VIEW,

    // Fleet & Vehicles
    VEHICLE_VIEW,
    VEHICLE_MANAGE,
    VEHICLE_VIEW_SELF,

    // Supply Chain & Essential Commodities
    SHIPMENT_VIEW,
    SHIPMENT_MANAGE,
    SHIPMENT_VIEW_SELF,

    // Incidents & Disaster Reporting
    INCIDENT_VIEW,
    INCIDENT_REPORT,
    INCIDENT_VERIFY,
    INCIDENT_RESOLVE,

    // Corridor & Road Accessibility
    ROAD_STATUS_VIEW,
    ROAD_STATUS_UPDATE,

    // Emergency SOS & Rescue Dispatch
    SOS_VIEW,
    SOS_VIEW_SELF,
    SOS_TRIGGER,
    SOS_ACKNOWLEDGE,
    SOS_DISPATCH,
    SOS_RESOLVE,

    // Routing & Decision Support
    ROUTE_VIEW,
    ROUTE_APPROVE,
    DELIVERY_STATUS_UPDATE,

    // Analytics & Governance Reporting
    ANALYTICS_VIEW,
    REPORT_EXPORT
}
