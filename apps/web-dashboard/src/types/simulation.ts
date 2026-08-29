export type SimulationStep = 1 | 2 | 3 | 4 | 5;

export interface SimulationState {
  step: SimulationStep;
  isRunning: boolean;
  isPaused: boolean;
  stepTitle: string;
  stepDescription: string;
  affectedVehicleId: string;
  primaryRouteBlocked: boolean;
  alternativeRouteActive: boolean;
  weatherAlertActive: boolean;
  incidentCreated: boolean;
  alertDispatched: boolean;
}
