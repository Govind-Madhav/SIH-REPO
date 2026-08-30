class OfflineEvent {
  final String clientEventId;
  final String eventType; // GPS_FIX, INCIDENT_REPORT, ACCESSIBILITY_REPORT, SOS_TRIGGER
  final String payloadJson;
  final DateTime createdAt;
  String syncStatus; // PENDING, SYNCING, SYNCED, FAILED
  int retryCount;
  String? lastError;

  OfflineEvent({
    required this.clientEventId,
    required this.eventType,
    required this.payloadJson,
    required this.createdAt,
    this.syncStatus = 'PENDING',
    this.retryCount = 0,
    this.lastError,
  });

  Map<String, dynamic> toMap() {
    return {
      'clientEventId': clientEventId,
      'eventType': eventType,
      'payloadJson': payloadJson,
      'createdAt': createdAt.toIso8601String(),
      'syncStatus': syncStatus,
      'retryCount': retryCount,
      'lastError': lastError,
    };
  }

  factory OfflineEvent.fromMap(Map<String, dynamic> map) {
    return OfflineEvent(
      clientEventId: map['clientEventId'],
      eventType: map['eventType'],
      payloadJson: map['payloadJson'],
      createdAt: DateTime.parse(map['createdAt']),
      syncStatus: map['syncStatus'] ?? 'PENDING',
      retryCount: map['retryCount'] ?? 0,
      lastError: map['lastError'],
    );
  }
}
