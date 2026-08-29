export type SupplyCategory = 'MEDICINE' | 'FOOD' | 'AGRICULTURE' | 'CONSTRUCTION';

export interface EssentialSupplySummary {
  category: SupplyCategory;
  name: string;
  inTransit: number;
  delayed: number;
  atRisk: number;
  delivered: number;
  criticalItem: string;
}

export interface Shipment {
  id: string;
  trackingNumber: string;
  category: SupplyCategory;
  description: string;
  assignedVehicleId: string;
  origin: string;
  destination: string;
  status: 'SCHEDULED' | 'IN_TRANSIT' | 'DELAYED' | 'REROUTED' | 'DELIVERED';
  priority: 'ROUTINE' | 'HIGH' | 'EMERGENCY_SOS';
  updatedAt: string;
}
