package com.mygate.visitor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Entry")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RailwayEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "qr_id")
    private Long qrId;
    
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;
    
    @Column(name = "user_type", nullable = false, length = 20)
    private String userType;
    
    @Column(name = "scanned_by", length = 100)
    private String scannedBy;
    
    @Column(name = "scan_location", length = 100)
    private String scanLocation;
    
    @Column(name = "person_name", length = 200)
    private String personName;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
