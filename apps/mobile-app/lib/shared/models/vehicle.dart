class Vehicle {
  final int? id;
  final String code;
  final String? licensePlate;
  final String? vehicleType;
  final double? capacityTons;
  final String status;

  Vehicle({
    this.id,
    required this.code,
    this.licensePlate,
    this.vehicleType,
    this.capacityTons,
    required this.status,
  });

  factory Vehicle.fromJson(Map<String, dynamic> json) {
    return Vehicle(
      id: json['id'],
      code: json['code'] ?? '',
      licensePlate: json['licensePlate'],
      vehicleType: json['vehicleType'],
      capacityTons: (json['capacityTons'] as num?)?.toDouble(),
      status: json['status'] ?? 'ON_TRACK',
    );
  }
}
