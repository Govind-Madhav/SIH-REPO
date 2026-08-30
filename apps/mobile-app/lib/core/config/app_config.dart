import 'package:flutter/foundation.dart';

class AppConfig {
  static String get apiBaseUrl {
    if (kIsWeb) return 'http://localhost:8080';
    if (defaultTargetPlatform == TargetPlatform.android) {
      // Android Emulator host loopback IP
      return 'http://10.0.2.2:8080';
    }
    return 'http://localhost:8080';
  }

  static String get wsBaseUrl {
    if (kIsWeb) return 'ws://localhost:8080/ws';
    if (defaultTargetPlatform == TargetPlatform.android) {
      return 'ws://10.0.2.2:8080/ws';
    }
    return 'ws://localhost:8080/ws';
  }

  static const bool useMockMode = false;
}
