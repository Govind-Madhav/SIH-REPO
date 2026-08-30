import 'package:flutter/material.dart';
import '../core/auth/secure_storage.dart';
import '../features/auth/login_screen.dart';
import '../features/driver/dashboard/driver_dashboard.dart';

class DriverApp extends StatefulWidget {
  const DriverApp({Key? key}) : super(key: key);

  @override
  State<DriverApp> createState() => _DriverAppState();
}

class _DriverAppState extends State<DriverApp> {
  final _secureStorage = SecureStorageService();
  bool _isLoggedIn = false;
  bool _isCheckingAuth = true;

  @override
  void initState() {
    super.initState();
    _checkAuthStatus();
  }

  Future<void> _checkAuthStatus() async {
    final token = await _secureStorage.getToken();
    setState(() {
      _isLoggedIn = token != null && token.isNotEmpty;
      _isCheckingAuth = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'NER Driver App',
      debugShowCheckedModeBanner: false,
      theme: ThemeData.dark().copyWith(
        scaffoldBackgroundColor: const Color(0xFF0F172A),
      ),
      home: _isCheckingAuth
          ? const Scaffold(
              body: Center(child: CircularProgressIndicator(color: Color(0xFF38BDF8))),
            )
          : _isLoggedIn
              ? DriverDashboard(onLogout: () => setState(() => _isLoggedIn = false))
              : LoginScreen(
                  appTitle: '🚛 NER Driver App',
                  onLoginSuccess: () => setState(() => _isLoggedIn = true),
                ),
    );
  }
}
