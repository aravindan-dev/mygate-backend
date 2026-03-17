package com.mygate.visitor.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_gatepass_requests")
public class VisitorGatepassRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "visitor_name", nullable = false, length = 120)
    private String visitorName;
    
    @Column(name = "visitor_phone", nullable = false, length = 20)
    private String visitorPhone;
    
    @Column(name = "visitor_email", length = 120)
    private String visitorEmail;
    
    @Column(name = "department", nullable = false, length = 80)
    private String department;
    
    @Column(name = "assigned_staff_id", nullable = false, length = 50)
    private String assignedStaffId;
    
    @Column(name = "purpose", length = 255)
    private String purpose;
    
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "visit_date")
    private LocalDate visitDate;
    
    @Column(name = "visit_time")
    private LocalTime visitTime;
    
    @Column(name = "request_status", length = 20)
    private String requestStatus = "PENDING";
    
    @Column(name = "staff_approval", length = 20)
    private String staffApproval = "PENDING";
    
    @Column(name = "staff_approved_at")
    private LocalDateTime staffApprovedAt;
    
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
    
    @Column(name = "qr_code_data", columnDefinition = "TEXT")
    private String qrCodeData;
    
    @Column(name = "manual_code", length = 10)
    private String manualCode; // Manual entry code for typing instead of scanning
    
    @Column(name = "qr_generated")
    private boolean qrGenerated = false;
    
    @Column(name = "qr_used")
    private boolean qrUsed = false;
    
    @Column(name = "qr_used_at")
    private LocalDateTime qrUsedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public VisitorGatepassRequest() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.requestStatus = "PENDING";
        this.staffApproval = "PENDING";
        this.qrGenerated = false;
        this.qrUsed = false;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getVisitorName() {
        return visitorName;
    }
    
    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }
    
    public String getVisitorPhone() {
        return visitorPhone;
    }
    
    public void setVisitorPhone(String visitorPhone) {
        this.visitorPhone = visitorPhone;
    }
    
    public String getVisitorEmail() {
        return visitorEmail;
    }
    
    public void setVisitorEmail(String visitorEmail) {
        this.visitorEmail = visitorEmail;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getAssignedStaffId() {
        return assignedStaffId;
    }
    
    public void setAssignedStaffId(String assignedStaffId) {
        this.assignedStaffId = assignedStaffId;
    }
    
    public String getPurpose() {
        return purpose;
    }
    
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDate getVisitDate() {
        return visitDate;
    }
    
    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }
    
    public LocalTime getVisitTime() {
        return visitTime;
    }
    
    public void setVisitTime(LocalTime visitTime) {
        this.visitTime = visitTime;
    }
    
    public String getRequestStatus() {
        return requestStatus;
    }
    
    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }
    
    public String getStaffApproval() {
        return staffApproval;
    }
    
    public void setStaffApproval(String staffApproval) {
        this.staffApproval = staffApproval;
    }
    
    public LocalDateTime getStaffApprovedAt() {
        return staffApprovedAt;
    }
    
    public void setStaffApprovedAt(LocalDateTime staffApprovedAt) {
        this.staffApprovedAt = staffApprovedAt;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public String getQrCodeData() {
        return qrCodeData;
    }
    
    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }
    
    public String getManualCode() {
        return manualCode;
    }
    
    public void setManualCode(String manualCode) {
        this.manualCode = manualCode;
    }
    
    public boolean getQrGenerated() {
        return qrGenerated;
    }
    
    public void setQrGenerated(boolean qrGenerated) {
        this.qrGenerated = qrGenerated;
    }
    
    public boolean getQrUsed() {
        return qrUsed;
    }
    
    public void setQrUsed(boolean qrUsed) {
        this.qrUsed = qrUsed;
    }
    
    public LocalDateTime getQrUsedAt() {
        return qrUsedAt;
    }
    
    public void setQrUsedAt(LocalDateTime qrUsedAt) {
        this.qrUsedAt = qrUsedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
