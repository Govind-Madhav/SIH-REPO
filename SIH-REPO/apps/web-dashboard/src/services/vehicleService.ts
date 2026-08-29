import { MOCK_VEHICLES } from '../data/mockData';
import { Vehicle, VehicleStatus } from '../types/vehicle';

export const vehicleService = {
  getVehicles: async (): Promise<Vehicle[]> => {
    // Simulated async API call to Spring Boot backend /api/v1/vehicles
    return new Promise((resolve) => {
      setTimeout(() => resolve([...MOCK_VEHICLES]), 100);
    });
  },

  getVehicleById: async (id: string): Promise<Vehicle | undefined> => {
    return MOCK_VEHICLES.find((v) => v.id === id);
  },

  updateVehicleStatus: async (
    id: string,
    status: VehicleStatus,
    riskLevel?: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
  ): Promise<Vehicle> => {
    const vehicle = MOCK_VEHICLES.find((v) => v.id === id);
    if (!vehicle) throw new Error('Vehicle not found');
    vehicle.status = status;
    if (riskLevel) vehicle.riskLevel = riskLevel;
    return { ...vehicle };
  },
};
