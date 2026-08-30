class ApiEndpoints {
  static const String login = '/api/auth/login';
  static const String sendOtp = '/api/auth/otp/send';
  static const String verifyOtp = '/api/auth/otp/verify';
  static const String me = '/api/auth/me';

  // Driver Mobile APIs
  static const String driverMe = '/api/mobile/driver/me';
  static const String driverVehicle = '/api/mobile/driver/me/vehicle';
  static const String driverShipments = '/api/mobile/driver/me/shipments';
  static const String driverRoute = '/api/mobile/driver/me/route';
  static const String driverSosStatus = '/api/mobile/driver/me/sos';

  // Tracking & Telemetry
  static const String trackLocation = '/api/tracking/location';
  static const String trackBatchLocation = '/api/tracking/location/batch';
  static String safetyBubble(String code) => '/api/tracking/safety-bubble/$code';

  // Incident & Evidence
  static const String incidents = '/api/incidents';
  static const String incidentSync = '/api/incidents/sync';
  static const String nearbyIncidents = '/api/incidents/nearby';
  static String incidentEvidence(int id) => '/api/incidents/$id/evidence';
  static const String uploadFile = '/api/files/upload';

  // Emergency SOS & P2P Mesh
  static const String sosTrigger = '/api/sos/trigger';
  static const String sosRelay = '/api/sos/relay';
  static const String sosAcks = '/api/sos/acks';

  // Corridor & Accessibility
  static const String accessibilityReport = '/api/accessibility/report';
  static const String corridors = '/api/accessibility/corridors';
  static const String districtHeatmap = '/api/accessibility/districts/heatmap';

  // Multilingual i18n
  static const String languages = '/api/i18n/languages';
  static const String translations = '/api/i18n/translations';
}
