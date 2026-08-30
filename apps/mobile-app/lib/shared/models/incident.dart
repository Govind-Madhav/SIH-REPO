class Incident {
  final int id;
  final String type;
  final String reportedSeverity;
  final String recommendedSeverity;
  final double severityScore;
  final double confidenceLevel;
  final String description;
  final double latitude;
  final double longitude;
  final String districtName;
  final String verificationStatus;
  final String status;

  Incident({
    required this.id,
    required this.type,
    required this.reportedSeverity,
    required this.recommendedSeverity,
    required this.severityScore,
    required this.confidenceLevel,
    required this.description,
    required this.latitude,
    required this.longitude,
    required this.districtName,
    required this.verificationStatus,
    required this.status,
  });

  factory Incident.fromJson(Map<String, dynamic> json) {
    return Incident(
      id: json['id'] ?? 0,
      type: json['type'] ?? '',
      reportedSeverity: json['reportedSeverity'] ?? '',
      recommendedSeverity: json['recommendedSeverity'] ?? '',
      severityScore: (json['severityScore'] as num?)?.toDouble() ?? 0.0,
      confidenceLevel: (json['confidenceLevel'] as num?)?.toDouble() ?? 0.0,
      description: json['description'] ?? '',
      latitude: (json['latitude'] as num?)?.toDouble() ?? 0.0,
      longitude: (json['longitude'] as num?)?.toDouble() ?? 0.0,
      districtName: json['districtName'] ?? '',
      verificationStatus: json['verificationStatus'] ?? 'UNDER_VERIFICATION',
      status: json['status'] ?? 'ACTIVE',
    );
  }
}
