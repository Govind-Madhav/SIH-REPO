export type VehicleStatus = 'ON_TRACK' | 'DELAYED' | 'AT_RISK' | 'OFFLINE';

export type ConnectivityState = 'ONLINE' | 'DEGRADED' | 'OFFLINE';

export interface GPSLocation {
  lat: number;
  lng: number;
  address?: string;
  speedKmH?: number;
  lastUpdated: string;
}

export interface Vehicle {
  id: string;
  code: string; // e.g. "NER-07"
  driverName: string;
  driverPhone: string;
  cargo: string;
  cargoCategory: 'MEDICINE' | 'FOOD' | 'AGRICULTURE' | 'CONSTRUCTION';
  status: VehicleStatus;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  connectivity: ConnectivityState;
  origin: string;
  destination: string;
  eta: string;
  location: GPSLocation;
  currentRouteId: string;
  alternativeRouteAvailable?: boolean;
}
