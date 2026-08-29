import React, { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { CommandHeader } from '../components/layout/CommandHeader';
import { Sidebar } from '../components/layout/Sidebar';
import { NERMap } from '../components/map/NERMap';
import { SimulationController } from '../components/dashboard/SimulationController';
import { AccessibilitySummary } from '../components/dashboard/AccessibilitySummary';
import { LiveAlertsPanel } from '../components/dashboard/LiveAlertsPanel';
import { RiskIntelligencePanel } from '../components/dashboard/RiskIntelligencePanel';
import { DistrictStatusPanel } from '../components/dashboard/DistrictStatusPanel';
import { EssentialSupplyPanel } from '../components/dashboard/EssentialSupplyPanel';
import { LiveSensorsPanel } from '../components/dashboard/LiveSensorsPanel';
import { SystemFlowDiagram } from '../components/dashboard/SystemFlowDiagram';
import { Vehicle } from '../types/vehicle';
import { Incident } from '../types/incident';
import { DistrictStatus } from '../types/district';
import { EssentialSupplySummary } from '../types/shipment';
import { SimulationState, SimulationStep } from '../types/simulation';
import { MOCK_VEHICLES, MOCK_INCIDENTS, MOCK_DISTRICTS, MOCK_ESSENTIAL_SUPPLIES } from '../data/mockData';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('dashboard');

  // Main Domain State
  const [vehicles, setVehicles] = useState<Vehicle[]>(MOCK_VEHICLES);
  const [incidents, setIncidents] = useState<Incident[]>(MOCK_INCIDENTS);
  const [districts, setDistricts] = useState<DistrictStatus[]>(MOCK_DISTRICTS);
  const [supplies, setSupplies] = useState<EssentialSupplySummary[]>(MOCK_ESSENTIAL_SUPPLIES);

  // Selected Items for Details View
  const [selectedVehicle, setSelectedVehicle] = useState<Vehicle | null>(null);
  const [selectedIncident, setSelectedIncident] = useState<Incident | null>(null);

  // Operational Simulation State
  const [simulationState, setSimulationState] = useState<SimulationState>({
    step: 1,
    isRunning: false,
    isPaused: false,
    stepTitle: 'Step 1: Normal Operation',
    stepDescription: 'Vehicle NER-07 in transit carrying Emergency Medicines along NH-27 Guwahati → Silchar.',
    affectedVehicleId: 'v-07',
    primaryRouteBlocked: false,
    alternativeRouteActive: false,
    weatherAlertActive: false,
    incidentCreated: false,
    alertDispatched: false,
  });

  // Simulated GPS Telemetry Motion
  useEffect(() => {
    const interval = setInterval(() => {
      setVehicles((prev) =>
        prev.map((v) => {
          if (v.code === 'NER-07') {
            const latDelta = (Math.random() - 0.5) * 0.002;
            const lngDelta = (Math.random() - 0.5) * 0.002;
            return {
              ...v,
              location: {
                ...v.location,
                lat: Number((v.location.lat + latDelta).toFixed(4)),
                lng: Number((v.location.lng + lngDelta).toFixed(4)),
                lastUpdated: 'Just now',
              },
            };
          }
          return v;
        })
      );
    }, 3000);

    return () => clearInterval(interval);
  }, []);

  // Automatic Simulation Stepping Loop
  useEffect(() => {
    let timer: NodeJS.Timeout;
    if (simulationState.isRunning && !simulationState.isPaused) {
      timer = setTimeout(() => {
        if (simulationState.step < 5) {
          jumpToSimulationStep((simulationState.step + 1) as SimulationStep);
        } else {
          setSimulationState((prev) => ({ ...prev, isRunning: false, isPaused: false }));
        }
      }, 4500);
    }
    return () => clearTimeout(timer);
  }, [simulationState.step, simulationState.isRunning, simulationState.isPaused]);

  // Jump to specific Simulation Step
  const jumpToSimulationStep = (step: SimulationStep) => {
    switch (step) {
      case 1:
        setSimulationState({
          step: 1,
          isRunning: true,
          isPaused: false,
          stepTitle: 'Step 1: Normal Operation',
          stepDescription: 'Vehicle NER-07 in transit carrying Emergency Medicines along NH-27 Guwahati → Silchar. Route Risk: LOW.',
          affectedVehicleId: 'v-07',
          primaryRouteBlocked: false,
          alternativeRouteActive: false,
          weatherAlertActive: false,
          incidentCreated: false,
          alertDispatched: false,
        });
        setVehicles((prev) =>
          prev.map((v) => (v.code === 'NER-07' ? { ...v, status: 'ON_TRACK', riskLevel: 'LOW', eta: '4h 20m' } : v))
        );
        break;

      case 2:
        setSimulationState({
          step: 2,
          isRunning: true,
          isPaused: false,
          stepTitle: 'Step 2: Weather Warning',
          stepDescription: '⚠ Heavy Torrential Rainfall (145mm/24h) detected in Dima Hasao Sector. Route Risk elevated to MEDIUM.',
          affectedVehicleId: 'v-07',
          primaryRouteBlocked: false,
          alternativeRouteActive: false,
          weatherAlertActive: true,
          incidentCreated: false,
          alertDispatched: false,
        });
        setVehicles((prev) =>
          prev.map((v) => (v.code === 'NER-07' ? { ...v, status: 'DELAYED', riskLevel: 'MEDIUM', eta: '4h 45m' } : v))
        );
        break;

      case 3:
        setSimulationState({
          step: 3,
          isRunning: true,
          isPaused: false,
          stepTitle: 'Step 3: Landslide Disruption Event',
          stepDescription: '🚨 CRITICAL LANDSLIDE REPORTED at Haflong Pass! Primary Corridor NH-27 BLOCKED.',
          affectedVehicleId: 'v-07',
          primaryRouteBlocked: true,
          alternativeRouteActive: false,
          weatherAlertActive: true,
          incidentCreated: true,
          alertDispatched: false,
        });
        setVehicles((prev) =>
          prev.map((v) => (v.code === 'NER-07' ? { ...v, status: 'AT_RISK', riskLevel: 'CRITICAL', eta: 'DELAYED (BLOCKED)' } : v))
        );
        break;

      case 4:
        setSimulationState({
          step: 4,
          isRunning: true,
          isPaused: false,
          stepTitle: 'Step 4: AI Intelligence Rerouting Engine',
          stepDescription: '✨ GraphHopper AI engine evaluated alternative bypass corridor (132 km - LOW RISK). Rerouting NER-07.',
          affectedVehicleId: 'v-07',
          primaryRouteBlocked: true,
          alternativeRouteActive: true,
          weatherAlertActive: true,
          incidentCreated: true,
          alertDispatched: false,
        });
        setVehicles((prev) =>
          prev.map((v) =>
            v.code === 'NER-07'
              ? {
                  ...v,
                  status: 'ON_TRACK',
                  riskLevel: 'LOW',
                  eta: '4h 45m (Alternative Pass)',
                  alternativeRouteAvailable: true,
                }
              : v
          )
        );
        break;

      case 5:
        setSimulationState({
          step: 5,
          isRunning: false,
          isPaused: false,
          stepTitle: 'Step 5: Automated Alert Dispatch',
          stepDescription: '🚨 ROUTE DISRUPTION ALERT dispatched to District Authority, Logistics Command & Driver App.',
          affectedVehicleId: 'v-07',
          primaryRouteBlocked: true,
          alternativeRouteActive: true,
          weatherAlertActive: true,
          incidentCreated: true,
          alertDispatched: true,
        });
        break;
    }
  };

  const handleStartSimulation = () => {
    if (simulationState.step === 5) {
      jumpToSimulationStep(1);
    } else {
      setSimulationState((prev) => ({ ...prev, isRunning: true, isPaused: false }));
    }
  };

  const handlePauseSimulation = () => {
    setSimulationState((prev) => ({ ...prev, isPaused: true }));
  };

  const handleResetSimulation = () => {
    jumpToSimulationStep(1);
    setSimulationState((prev) => ({ ...prev, isRunning: false, isPaused: false }));
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 flex flex-col font-sans">
      {/* Top Header */}
      <CommandHeader activeStep={simulationState.step} onResetSimulation={handleResetSimulation} />

      <div className="flex flex-1 overflow-hidden">
        {/* Navigation Sidebar */}
        <Sidebar activeTab={activeTab} onTabChange={setActiveTab} />

        {/* Main Content Area */}
        <main className="flex-1 p-4 overflow-y-auto space-y-4 max-w-[1920px] mx-auto w-full">
          {/* Simulation Controller Bar */}
          <SimulationController
            state={simulationState}
            onStart={handleStartSimulation}
            onPause={handlePauseSimulation}
            onReset={handleResetSimulation}
            onJumpToStep={jumpToSimulationStep}
          />

          {/* Top Accessibility Metrics Summary Bar */}
          <AccessibilitySummary
            districtAccessiblePct={68}
            activeVehiclesCount={vehicles.length}
            activeIncidentsCount={incidents.length}
            highRiskCorridorsCount={5}
            delayedShipmentsCount={supplies.reduce((acc, s) => acc + s.delayed, 0)}
          />

          {/* Central Hero Grid: Map + Right Panel */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 min-h-[580px]">
            {/* Interactive Leaflet Map Hero (Takes 2 Columns) */}
            <div className="lg:col-span-2 min-h-[520px]">
              <NERMap
                vehicles={vehicles}
                incidents={incidents}
                districts={districts}
                simulationState={simulationState}
                onSelectVehicle={(v) => setSelectedVehicle(v)}
                onSelectIncident={(inc) => setSelectedIncident(inc)}
              />
            </div>

            {/* Right Side Column: Live Alerts & AI Risk Intelligence */}
            <div className="space-y-4 flex flex-col justify-between">
              <RiskIntelligencePanel
                overallRisk={
                  simulationState.step >= 3 ? 'CRITICAL' : simulationState.step === 2 ? 'HIGH' : 'LOW'
                }
                weatherImpactPct={simulationState.step >= 2 ? 85 : 30}
                roadConditionPct={simulationState.step >= 3 ? 90 : 40}
                historicalRiskPct={55}
                fieldReportsPct={simulationState.step >= 3 ? 88 : 45}
              />

              <div className="flex-1 min-h-[300px]">
                <LiveAlertsPanel />
              </div>
            </div>
          </div>

          {/* Bottom Grid: District Connectivity, Essential Supply, Live Sensors */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <DistrictStatusPanel districts={districts} />
            <EssentialSupplyPanel supplies={supplies} />
            <LiveSensorsPanel />
          </div>

          {/* Platform Intelligence Pipeline Flow Diagram */}
          <SystemFlowDiagram />
        </main>
      </div>
    </div>
  );
};
