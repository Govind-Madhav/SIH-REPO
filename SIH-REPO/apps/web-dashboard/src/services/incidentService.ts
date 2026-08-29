import { MOCK_INCIDENTS } from '../data/mockData';
import { Incident } from '../types/incident';

export const incidentService = {
  getIncidents: async (): Promise<Incident[]> => {
    return new Promise((resolve) => {
      setTimeout(() => resolve([...MOCK_INCIDENTS]), 100);
    });
  },

  addIncident: async (incident: Incident): Promise<Incident> => {
    MOCK_INCIDENTS.unshift(incident);
    return incident;
  },
};
