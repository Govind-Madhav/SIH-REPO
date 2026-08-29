export type AccessibilityStatus = 'ACCESSIBLE' | 'PARTIAL_ACCESS' | 'INACCESSIBLE';

export interface DistrictStatus {
  id: string;
  name: string;
  state: 'Assam' | 'Meghalaya' | 'Manipur' | 'Nagaland' | 'Mizoram' | 'Tripura' | 'Arunachal Pradesh' | 'Sikkim';
  accessibilityPercentage: number;
  status: AccessibilityStatus;
  activeIncidentsCount: number;
  vehiclesOperatingCount: number;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  weatherAlert?: string;
  center: [number, number]; // [lat, lng]
}
