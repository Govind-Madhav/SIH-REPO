package com.ner.logistics.mobile;

import com.ner.logistics.shipment.Shipment;
import com.ner.logistics.sos.SosEvent;
import com.ner.logistics.vehicle.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverAssignmentDto {
    private String driverUsername;
    private String fullName;
    private String role;
    private Vehicle assignedVehicle;
    private List<Shipment> assignedShipments;
    private SosEvent activeSosEvent;
}
