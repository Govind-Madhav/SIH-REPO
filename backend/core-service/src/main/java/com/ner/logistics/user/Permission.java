package com.ner.logistics.user;

public enum Permission {
    // Governance & System Admin
    USER_MANAGE,
    USER_CREATE,
    USER_VIEW,
    USER_UPDATE,
    USER_SUSPEND,
    USER_REACTIVATE,
    USER_DEACTIVATE,
    ROLE_MANAGE,
    DEVICE_MANAGE,
    SYSTEM_CONFIG_MANAGE,
    AUDIT_LOG_VIEW,

    // ML Governance & Operational Parameters
    RISK_THRESHOLD_MANAGE,
    MODEL_VERSION_VIEW,
    MODEL_DEPLOY,
    MODEL_ROLLBACK,
    MODEL_PERFORMANCE_VIEW,

    // Alert Policy & Multilingual Templates
    ALERT_POLICY_MANAGE,
    NOTIFICATION_TEMPLATE_MANAGE,
    LANGUAGE_CONFIGURATION_MANAGE,

    // External Integration & Health
    INTEGRATION_MANAGE,
    INTEGRATION_HEALTH_VIEW,
    API_CREDENTIAL_MANAGE,
    SYSTEM_HEALTH_VIEW,

    // Device Assignment & Telematics Lifecycle
    DEVICE_ASSIGN,
    DEVICE_UNASSIGN,
    DEVICE_STATUS_VIEW,

    // Geofencing & Corridor Intelligence
    GEOFENCE_MANAGE,
    RISK_CORRIDOR_MANAGE,

    // Emergency Resource Management
    EMERGENCY_RESOURCE_MANAGE,

    // Data Retention & Archival Policies
    DATA_RETENTION_MANAGE,
    ARCHIVE_STATUS_VIEW,
    SENSITIVE_DATA_EXPORT,

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
