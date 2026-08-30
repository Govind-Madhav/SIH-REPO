import 'dart:convert';
import 'package:connectivity_plus/connectivity_plus.dart';
import '../api/api_client.dart';
import '../api/api_endpoints.dart';
import 'offline_queue_service.dart';

class SyncEngine {
  final ApiClient _apiClient;
  final OfflineQueueService _queueService;
  final Connectivity _connectivity;

  SyncEngine({
    ApiClient? apiClient,
    OfflineQueueService? queueService,
    Connectivity? connectivity,
  })  : _apiClient = apiClient ?? ApiClient(),
        _queueService = queueService ?? OfflineQueueService(),
        _connectivity = connectivity ?? Connectivity() {
    _initConnectivityListener();
  }

  void _initConnectivityListener() {
    _connectivity.onConnectivityChanged.listen((result) {
      if (result != ConnectivityResult.none) {
        flushQueue();
      }
    });
  }

  Future<void> flushQueue() async {
    final pending = await _queueService.getPendingEvents();
    if (pending.isEmpty) return;

    final incidentDtos = <dynamic>[];
    final gpsEvents = <dynamic>[];

    for (final event in pending) {
      final payload = jsonDecode(event.payloadJson);

      if (event.eventType == 'INCIDENT_REPORT') {
        payload['clientGeneratedId'] = event.clientEventId;
        incidentDtos.add(payload);
      } else if (event.eventType == 'GPS_FIX') {
        payload['clientEventId'] = event.clientEventId;
        gpsEvents.add(payload);
      }
    }

    // Flush Incident Queue
    if (incidentDtos.isNotEmpty) {
      try {
        await _apiClient.post(ApiEndpoints.incidentSync, body: incidentDtos);
        for (final event in pending.where((e) => e.eventType == 'INCIDENT_REPORT')) {
          await _queueService.markSynced(event.clientEventId);
        }
      } catch (e) {
        // Log sync error
      }
    }

    // Flush Batch GPS Queue
    if (gpsEvents.isNotEmpty) {
      try {
        await _apiClient.post(ApiEndpoints.trackBatchLocation, body: {'events': gpsEvents});
        for (final event in pending.where((e) => e.eventType == 'GPS_FIX')) {
          await _queueService.markSynced(event.clientEventId);
        }
      } catch (e) {
        // Log sync error
      }
    }
  }
}
