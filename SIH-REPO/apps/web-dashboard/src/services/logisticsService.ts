import { MOCK_ESSENTIAL_SUPPLIES } from '../data/mockData';
import { EssentialSupplySummary } from '../types/shipment';

export const logisticsService = {
  getEssentialSupplySummary: async (): Promise<EssentialSupplySummary[]> => {
    return new Promise((resolve) => {
      setTimeout(() => resolve([...MOCK_ESSENTIAL_SUPPLIES]), 100);
    });
  },
};
