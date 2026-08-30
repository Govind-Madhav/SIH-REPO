import 'package:flutter/material.dart';
import '../../../core/api/api_client.dart';
import '../../../core/api/api_endpoints.dart';
import '../../../core/auth/secure_storage.dart';
import '../../../shared/models/shipment.dart';
import '../../../shared/models/vehicle.dart';

class DriverDashboard extends StatefulWidget {
  final VoidCallback onLogout;

  const DriverDashboard({Key? key, required this.onLogout}) : super(key: key);

  @override
  State<DriverDashboard> createState() => _DriverDashboardState();
}

class _DriverDashboardState extends State<DriverDashboard> {
  final _apiClient = ApiClient();
  final _secureStorage = SecureStorageService();

  Vehicle? _vehicle;
  List<Shipment> _shipments = [];
  String _safetyZone = 'SAFE_ZONE';
  bool _isLoading = true;
  String? _statusMsg;

  @override
  void initState() {
    super.initState();
    _loadDriverData();
  }

  Future<void> _loadDriverData() async {
    setState(() => _isLoading = true);
    try {
      final res = await _apiClient.get(ApiEndpoints.driverMe);
      if (res != null) {
        if (res['assignedVehicle'] != null) {
          _vehicle = Vehicle.fromJson(res['assignedVehicle']);
        }
        if (res['assignedShipments'] != null) {
          _shipments = (res['assignedShipments'] as List)
              .map((s) => Shipment.fromJson(s))
              .toList();
        }
      }

      final bubble = await _apiClient.get(ApiEndpoints.safetyBubble('NER-07'));
      if (bubble != null) {
        _safetyZone = bubble['safetyZone'] ?? 'SAFE_ZONE';
      }
    } catch (e) {
      _statusMsg = 'Error loading driver data: $e';
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _triggerSos() async {
    try {
      await _apiClient.post(
        ApiEndpoints.sosTrigger,
        body: {
          'vehicleCode': _vehicle?.code ?? 'NER-07',
          'latitude': 25.1234,
          'longitude': 92.5678,
          'emergencyType': 'LANDSLIDE_TRAPPED',
          'message': 'Driver triggered SOS panic signal',
        },
      );
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('🚨 SOS Panic Signal Triggered Successfully!'),
          backgroundColor: Colors.red,
        ),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('SOS Trigger Failed: $e')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0F172A),
      appBar: AppBar(
        title: const Text('🚛 NER Convoy Driver Dashboard'),
        backgroundColor: const Color(0xFF1E293B),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              await _secureStorage.clearAll();
              widget.onLogout();
            },
          )
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator(color: Color(0xFF38BDF8)))
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  // Safety Zone Banner
                  Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      color: _safetyZone == 'DANGER_ZONE' ? const Color(0xFF991B1B) : const Color(0xFF166534),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Row(
                      children: [
                        Icon(
                          _safetyZone == 'DANGER_ZONE' ? Icons.warning_amber : Icons.verified_user_outlined,
                          color: Colors.white,
                          size: 32,
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'SAFETY BUBBLE: $_safetyZone',
                                style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 16),
                              ),
                              Text(
                                _safetyZone == 'DANGER_ZONE'
                                    ? '⚠️ Approaching Haflong landslide hazard zone!'
                                    : '🟢 Corridor is clear for convoy transport.',
                                style: const TextStyle(color: Colors.white70, fontSize: 13),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 16),
                  // Vehicle Card
                  Card(
                    color: const Color(0xFF1E293B),
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('Assigned Convoy Vehicle', style: TextStyle(color: Color(0xFF94A3B8), fontSize: 13)),
                          const SizedBox(height: 4),
                          Text(
                            _vehicle?.code ?? 'NER-07',
                            style: const TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.bold),
                          ),
                          Text(
                            'Plate: ${_vehicle?.licensePlate ?? "AS-01-HA-7007"} • Status: ${_vehicle?.status ?? "ON_TRACK"}',
                            style: const TextStyle(color: Color(0xFF38BDF8), fontSize: 14),
                          ),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  // Shipment Card
                  Card(
                    color: const Color(0xFF1E293B),
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('Assigned Essential Cargo Shipment', style: TextStyle(color: Color(0xFF94A3B8), fontSize: 13)),
                          const SizedBox(height: 4),
                          Text(
                            _shipments.isNotEmpty ? _shipments.first.commodityType : 'MEDICINE (Critical Relief)',
                            style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            'Route: ${_shipments.isNotEmpty ? _shipments.first.origin : "Guwahati"} -> ${_shipments.isNotEmpty ? _shipments.first.destination : "Silchar Hospital"}',
                            style: const TextStyle(color: Color(0xFFCBD5E1), fontSize: 13),
                          ),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(height: 24),
                  // SOS Trigger Button
                  ElevatedButton.icon(
                    onPressed: _triggerSos,
                    icon: const Icon(Icons.emergency, color: Colors.white, size: 28),
                    label: const Text('EMERGENCY SOS PANIC BUTTON', style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.red,
                      padding: const EdgeInsets.symmetric(vertical: 18),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                    ),
                  ),
                ],
              ),
            ),
    );
  }
}
