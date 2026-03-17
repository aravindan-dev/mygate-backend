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
public class RailwayExitLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "qr_id")
    private Long qrId;
    
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;
    
    @Column(name = "user_type", nullable = false, length = 20)
    private String userType;
    
    @Column(name = "exit_time", nullable = false)
    private LocalDateTime exitTime;
    
    @Column(name = "verified_by", length = 100)
    private String verifiedBy;
    
    @Column(name = "location", length = 100)
    private String location;
    
    @Column(name = "person_name", length = 200)
    private String personName;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(name = "email", length = 200)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "purpose", length = 500)
    private String purpose;
    
    @Column(name = "destination", length = 200)
    private String destination;
    
    @Column(name = "qr_code", length = 500)
    private String qrCode;
    
    @Column(name = "scan_location", length = 100)
    private String scanLocation;
    
    @Column(name = "device_id", length = 100)
    private String deviceId;
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    @Column(name = "access_granted")
    private Boolean accessGranted = true;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @PrePersist
    protected void onCreate() {
        if (exitTime == null) {
            exitTime = LocalDateTime.now();
        }
    }
}
