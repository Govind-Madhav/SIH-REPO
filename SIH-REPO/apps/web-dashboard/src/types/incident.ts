export type IncidentType = 'LANDSLIDE' | 'FLOOD' | 'ROAD_DAMAGE' | 'BRIDGE_ISSUE';

export type IncidentSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type IncidentStatus = 'ACTIVE' | 'INVESTIGATING' | 'CLEARING' | 'RESOLVED';

export interface Incident {
  id: string;
  type: IncidentType;
  title: string;
  severity: IncidentSeverity;
  status: IncidentStatus;
  location: {
    lat: number;
    lng: number;
    name: string;
    district: string;
  };
  affectedRoute: string;
  reportedTime: string;
  source: 'Field Officer Report' | 'Weather API Sensor' | 'Drone Inspection' | 'Citizen Report';
  description: string;
  photoUrl?: string;
}
