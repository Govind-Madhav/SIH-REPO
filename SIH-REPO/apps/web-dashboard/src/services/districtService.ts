import { MOCK_DISTRICTS } from '../data/mockData';
import { DistrictStatus } from '../types/district';

export const districtService = {
  getDistricts: async (): Promise<DistrictStatus[]> => {
    return new Promise((resolve) => {
      setTimeout(() => resolve([...MOCK_DISTRICTS]), 100);
    });
  },
};
