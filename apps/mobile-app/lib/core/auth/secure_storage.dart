import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class SecureStorageService {
  static const _storage = FlutterSecureStorage();
  static const String _keyJwtToken = 'jwt_token';
  static const String _keyUserJson = 'user_json';

  Future<void> saveToken(String token) async {
    await _storage.write(key: _keyJwtToken, value: token);
  }

  Future<String?> getToken() async {
    return await _storage.read(key: _keyJwtToken);
  }

  Future<void> saveUserData(String jsonStr) async {
    await _storage.write(key: _keyUserJson, value: jsonStr);
  }

  Future<String?> getUserData() async {
    return await _storage.read(key: _keyUserJson);
  }

  Future<void> clearAll() async {
    await _storage.deleteAll();
  }
}
