import 'dart:convert';
import 'package:http/http.dart' as http;
import '../auth/secure_storage.dart';
import '../config/app_config.dart';

class ApiClient {
  final http.Client _client;
  final SecureStorageService _secureStorage;

  ApiClient({http.Client? client, SecureStorageService? secureStorage})
      : _client = client ?? http.Client(),
        _secureStorage = secureStorage ?? SecureStorageService();

  Future<Map<String, String>> _getHeaders({bool authRequired = true}) async {
    final headers = <String, String>{
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };

    if (authRequired) {
      final token = await _secureStorage.getToken();
      if (token != null && token.isNotEmpty) {
        headers['Authorization'] = 'Bearer $token';
      }
    }
    return headers;
  }

  Future<dynamic> get(String path, {bool authRequired = true}) async {
    final url = Uri.parse('${AppConfig.apiBaseUrl}$path');
    final headers = await _getHeaders(authRequired: authRequired);
    final response = await _client.get(url, headers: headers);
    return _processResponse(response);
  }

  Future<dynamic> post(String path, {dynamic body, bool authRequired = true}) async {
    final url = Uri.parse('${AppConfig.apiBaseUrl}$path');
    final headers = await _getHeaders(authRequired: authRequired);
    final response = await _client.post(
      url,
      headers: headers,
      body: body != null ? jsonEncode(body) : null,
    );
    return _processResponse(response);
  }

  Future<dynamic> put(String path, {dynamic body, bool authRequired = true}) async {
    final url = Uri.parse('${AppConfig.apiBaseUrl}$path');
    final headers = await _getHeaders(authRequired: authRequired);
    final response = await _client.put(
      url,
      headers: headers,
      body: body != null ? jsonEncode(body) : null,
    );
    return _processResponse(response);
  }

  dynamic _processResponse(http.Response response) {
    final statusCode = response.statusCode;
    if (statusCode >= 200 && statusCode < 300) {
      if (response.body.isEmpty) return null;
      return jsonDecode(response.body);
    } else if (statusCode == 401) {
      _secureStorage.clearAll();
      throw Exception('Unauthorized access (401). Session expired.');
    } else if (statusCode == 403) {
      throw Exception('Forbidden access (403). Insufficient permissions.');
    } else {
      throw Exception('Server Error ($statusCode): ${response.body}');
    }
  }
}
