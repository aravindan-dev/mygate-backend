package com.mygate.visitor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bulk_gatepass_students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkGatepassStudent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pass_request_id", nullable = false)
    private Long passRequestId; // FK to gate_pass_requests
    
    @Column(name = "reg_no", nullable = false)
    private String regNo; // Student registration number
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructor for easy creation
    public BulkGatepassStudent(Long passRequestId, String regNo) {
        this.passRequestId = passRequestId;
        this.regNo = regNo;
    }
}
