import 'package:flutter/material.dart';
import '../core/auth/secure_storage.dart';
import '../features/auth/login_screen.dart';
import '../features/field_officer/dashboard/officer_dashboard.dart';

class FieldOfficerApp extends StatefulWidget {
  const FieldOfficerApp({Key? key}) : super(key: key);

  @override
  State<FieldOfficerApp> createState() => _FieldOfficerAppState();
}

class _FieldOfficerAppState extends State<FieldOfficerApp> {
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
      title: 'NER Field Officer App',
      debugShowCheckedModeBanner: false,
      theme: ThemeData.dark().copyWith(
        scaffoldBackgroundColor: const Color(0xFF0F172A),
      ),
      home: _isCheckingAuth
          ? const Scaffold(
              body: Center(child: CircularProgressIndicator(color: Color(0xFF38BDF8))),
            )
          : _isLoggedIn
              ? OfficerDashboard(onLogout: () => setState(() => _isLoggedIn = false))
              : LoginScreen(
                  appTitle: '👷 NER Field Officer App',
                  onLoginSuccess: () => setState(() => _isLoggedIn = true),
                ),
    );
  }
}
