import 'dart:convert';
import 'package:stomp_dart_client/stomp_dart_client.dart';
import '../config/app_config.dart';

class StompClientService {
  StompClient? _stompClient;

  void connect({
    Function(Map<String, dynamic>)? onSosAlert,
    Function(Map<String, dynamic>)? onRouteUpdate,
    Function(Map<String, dynamic>)? onRiskAlert,
  }) {
    _stompClient = StompClient(
      config: StompConfig(
        url: AppConfig.wsBaseUrl,
        onConnect: (StompFrame frame) {
          if (onSosAlert != null) {
            _stompClient?.subscribe(
              destination: '/topic/sos-alerts',
              callback: (frame) {
                if (frame.body != null) {
                  onSosAlert(jsonDecode(frame.body!));
                }
              },
            );
          }

          if (onRouteUpdate != null) {
            _stompClient?.subscribe(
              destination: '/topic/route-updates',
              callback: (frame) {
                if (frame.body != null) {
                  onRouteUpdate(jsonDecode(frame.body!));
                }
              },
            );
          }

          if (onRiskAlert != null) {
            _stompClient?.subscribe(
              destination: '/topic/risk-alerts',
              callback: (frame) {
                if (frame.body != null) {
                  onRiskAlert(jsonDecode(frame.body!));
                }
              },
            );
          }
        },
        onWebSocketError: (error) {},
      ),
    );

    _stompClient?.activate();
  }

  void disconnect() {
    _stompClient?.deactivate();
  }
}
