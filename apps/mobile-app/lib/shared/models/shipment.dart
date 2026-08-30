class Shipment {
  final int id;
  final String vehicleCode;
  final String commodityType;
  final String priority;
  final String origin;
  final String destination;
  final String status;

  Shipment({
    required this.id,
    required this.vehicleCode,
    required this.commodityType,
    required this.priority,
    required this.origin,
    required this.destination,
    required this.status,
  });

  factory Shipment.fromJson(Map<String, dynamic> json) {
    return Shipment(
      id: json['id'] ?? 0,
      vehicleCode: json['vehicleCode'] ?? '',
      commodityType: json['commodityType'] ?? '',
      priority: json['priority'] ?? 'NORMAL',
      origin: json['origin'] ?? '',
      destination: json['destination'] ?? '',
      status: json['status'] ?? 'IN_TRANSIT',
    );
  }
}
