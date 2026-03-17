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
public class EntryExit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_type", nullable = false)
    private String userType; // STUDENT, STAFF, VISITOR
    
    @Column(name = "user_id", nullable = false)
    private String userId; // regNo, staffCode, or visitor ID
    
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Column(name = "user_email")
    private String userEmail;
    
    @Column(name = "action", nullable = false)
    private String action; // ENTRY, EXIT
    
    @Column(name = "location")
    private String location; // Main Gate, Side Gate, etc.
    
    @Column(name = "purpose")
    private String purpose; // Personal, Official, Medical, etc.
    
    @Column(name = "destination")
    private String destination;
    
    @Column(name = "device_id")
    private String deviceId;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "gate_pass_id")
    private Long gatePassId; // Optional: if using gate pass
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
