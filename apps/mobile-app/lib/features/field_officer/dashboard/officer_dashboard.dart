import 'package:flutter/material.dart';
import '../../../core/api/api_client.dart';
import '../../../core/api/api_endpoints.dart';
import '../../../core/auth/secure_storage.dart';
import '../../../shared/models/incident.dart';

class OfficerDashboard extends StatefulWidget {
  final VoidCallback onLogout;

  const OfficerDashboard({Key? key, required this.onLogout}) : super(key: key);

  @override
  State<OfficerDashboard> createState() => _OfficerDashboardState();
}

class _OfficerDashboardState extends State<OfficerDashboard> {
  final _apiClient = ApiClient();
  final _secureStorage = SecureStorageService();

  final _typeController = TextEditingController(text: 'LANDSLIDE');
  final _descController = TextEditingController(text: 'Haflong Pass debris blockage');

  List<Incident> _nearbyIncidents = [];
  bool _isLoading = false;
  String? _statusMsg;

  @override
  void initState() {
    super.initState();
    _loadNearbyIncidents();
  }

  Future<void> _loadNearbyIncidents() async {
    try {
      final list = await _apiClient.get('${ApiEndpoints.nearbyIncidents}?lat=25.1234&lng=92.5678&distanceMeters=10000');
      if (list != null && list is List) {
        setState(() {
          _nearbyIncidents = list.map((i) => Incident.fromJson(i)).toList();
        });
      }
    } catch (e) {
      // Ignore error for offline test
    }
  }

  Future<void> _submitIncident() async {
    setState(() => _isLoading = true);
    try {
      await _apiClient.post(
        ApiEndpoints.incidents,
        body: {
          'type': _typeController.text.trim(),
          'reportedSeverity': 'CRITICAL',
          'description': _descController.text.trim(),
          'latitude': 25.1234,
          'longitude': 92.5678,
        },
      );
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('🚨 Geo-tagged Incident Reported Successfully!'), backgroundColor: Colors.green),
      );
      _loadNearbyIncidents();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed to report incident: $e')),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _submitRoadBlockage() async {
    try {
      await _apiClient.post(
        ApiEndpoints.accessibilityReport,
        body: {
          'latitude': 25.1234,
          'longitude': 92.5678,
          'status': 'BLOCKED',
          'condition': 'LANDSLIDE',
          'description': 'NH-27 mountain pass debris blockage',
          'corridorCode': 'COR-NH27',
        },
      );
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('🛣️ Road Accessibility Report Submitted! Routing Pipeline Updated.'), backgroundColor: Colors.orange),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Accessibility report failed: $e')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0F172A),
      appBar: AppBar(
        title: const Text('👷 NER Field Officer Dashboard'),
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
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Incident Report Card
            Card(
              color: const Color(0xFF1E293B),
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('Report Geo-Tagged Disaster Incident', style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 12),
                    TextField(
                      controller: _typeController,
                      style: const TextStyle(color: Colors.white),
                      decoration: const InputDecoration(
                        labelText: 'Incident Type (LANDSLIDE / FLOOD)',
                        labelStyle: TextStyle(color: Color(0xFF94A3B8)),
                        enabledBorder: OutlineInputBorder(borderSide: BorderSide(color: Color(0xFF475569))),
                      ),
                    ),
                    const SizedBox(height: 12),
                    TextField(
                      controller: _descController,
                      style: const TextStyle(color: Colors.white),
                      decoration: const InputDecoration(
                        labelText: 'Disruption Description',
                        labelStyle: TextStyle(color: Color(0xFF94A3B8)),
                        enabledBorder: OutlineInputBorder(borderSide: BorderSide(color: Color(0xFF475569))),
                      ),
                    ),
                    const SizedBox(height: 16),
                    ElevatedButton.icon(
                      onPressed: _isLoading ? null : _submitIncident,
                      icon: const Icon(Icons.add_location_alt, color: Colors.white),
                      label: const Text('Submit Geo-Tagged Incident Report', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF0284C7),
                        padding: const EdgeInsets.symmetric(vertical: 14),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            // Road Blockage Quick Action
            ElevatedButton.icon(
              onPressed: _submitRoadBlockage,
              icon: const Icon(Icons.block, color: Colors.white),
              label: const Text('Report Road Blockage on NH-27 Corridor', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFD97706),
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
            ),
            const SizedBox(height: 24),
            const Text('Nearby Disruption Incidents', style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            _nearbyIncidents.isEmpty
                ? const Text('No active incidents reported in this sector.', style: TextStyle(color: Color(0xFF94A3B8)))
                : ListView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: _nearbyIncidents.length,
                    itemBuilder: (context, index) {
                      final inc = _nearbyIncidents[index];
                      return Card(
                        color: const Color(0xFF334155),
                        child: ListTile(
                          title: Text('${inc.type} - ${inc.reportedSeverity}', style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                          subtitle: Text(inc.description, style: const TextStyle(color: Color(0xFFCBD5E1))),
                        ),
                      );
                    },
                  ),
          ],
        ),
      ),
    );
  }
}
