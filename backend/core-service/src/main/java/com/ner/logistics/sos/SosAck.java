package com.ner.logistics.sos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sos_acks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SosAck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String meshPacketId;

    private String originVehicleCode;

    private String status; // DELIVERED, RESCUE_DISPATCHED

    private String dispatchDetails; // e.g. "Haflong Rescue Unit 02 dispatched with medical kit"

    private LocalDateTime ackTimestamp;
}
