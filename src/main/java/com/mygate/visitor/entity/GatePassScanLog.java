package com.mygate.visitor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Exit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GatePassScanLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pass_id", nullable = false)
    private Long passId; // FK to gate_pass_requests
    
    @Enumerated(EnumType.STRING)
    @Column(name = "scan_type", nullable = false)
    private ScanType scanType; // ENTRY, EXIT
    
    @Column(name = "scanned_by")
    private String scannedBy; // Security personnel ID
    
    @Column(name = "gate_id")
    private String gateId; // Gate location
    
    @Column(name = "device_id")
    private String deviceId;
    
    @Column(name = "scanned_at", nullable = false)
    private LocalDateTime scannedAt;
    
    @PrePersist
    protected void onCreate() {
        if (scannedAt == null) {
            scannedAt = LocalDateTime.now();
        }
    }
    
    // Enum for scan type
    public enum ScanType {
        ENTRY,
        EXIT
    }
    
    // Constructor for easy creation
    public GatePassScanLog(Long passId, ScanType scanType, String scannedBy, 
                          String gateId, String deviceId) {
        this.passId = passId;
        this.scanType = scanType;
        this.scannedBy = scannedBy;
        this.gateId = gateId;
        this.deviceId = deviceId;
    }
}
