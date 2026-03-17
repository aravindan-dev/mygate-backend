package com.mygate.visitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "QR")
public class QRTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pass_request_id")
    private Long passRequestId;
    
    @Column(name = "requested_by_staff_code", nullable = false, length = 50)
    private String requestedByStaffCode;
    
    @Column(name = "qr_string", nullable = false, length = 2000)
    private String qrString;
    
    @Column(name = "manual_entry_code", length = 10)
    private String manualEntryCode;
    
    @Column(name = "pass_type", nullable = false, length = 20)
    private String passType = "BULK";
    
    @Column(name = "include_staff")
    private boolean includeStaff = true;
    
    @Column(name = "student_count", nullable = false)
    private Integer studentCount;
    
    @Column(name = "staff_count")
    private Integer staffCount = 0;
    
    @Column(length = 20, nullable = false)
    private String status = "ACTIVE";
    
    @Column(name = "entry_scanned_at")
    private LocalDateTime entryScannedAt;
    
    @Column(name = "exit_scanned_at")
    private LocalDateTime exitScannedAt;
    
    @Column(name = "entry_scanned_by", length = 50)
    private String entryScannedBy; // Security personnel who scanned entry
    
    @Column(name = "exit_scanned_by", length = 50)
    private String exitScannedBy; // Security personnel who scanned exit
    
    @Column(name = "signature", length = 100)
    private String signature; // HMAC-SHA256 signature for QR validation
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(length = 12)
    private String entry; // Timestamp marker for entry
    
    @Column(name = "`exit`", length = 12)
    private String exit; // Timestamp marker for exit
    
    @Column(name = "qr_code", unique = true, length = 100)
    private String qrCode; // Stores the random token
    
    @Column(name = "user_id", length = 50)
    private String userId;
    
    @Column(name = "user_type", length = 2)
    private String userType; // GP for group pass
    
    @Column(name = "group_type", length = 20)
    private String groupType = "STUDENTS_ONLY";
    
    // Constructors
    public QRTable() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor for bulk/group passes
    public QRTable(Long passRequestId, String requestedByStaffCode, String qrString, 
                   String manualEntryCode, String passType, Boolean includeStaff, Integer studentCount) {
        this.passRequestId = passRequestId;
        this.requestedByStaffCode = requestedByStaffCode;
        this.qrString = qrString;
        this.manualEntryCode = manualEntryCode;
        this.passType = passType;
        this.includeStaff = includeStaff != null ? includeStaff : false;
        this.studentCount = studentCount;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getPassRequestId() { return passRequestId; }
    public void setPassRequestId(Long passRequestId) { this.passRequestId = passRequestId; }
    
    public String getRequestedByStaffCode() { return requestedByStaffCode; }
    public void setRequestedByStaffCode(String requestedByStaffCode) { this.requestedByStaffCode = requestedByStaffCode; }
    
    public String getQrString() { return qrString; }
    public void setQrString(String qrString) { this.qrString = qrString; }
    
    public String getManualEntryCode() { return manualEntryCode; }
    public void setManualEntryCode(String manualEntryCode) { this.manualEntryCode = manualEntryCode; }
    
    public String getPassType() { return passType; }
    public void setPassType(String passType) { this.passType = passType; }
    
    public boolean getIncludeStaff() { return includeStaff; }
    public void setIncludeStaff(boolean includeStaff) { this.includeStaff = includeStaff; }
    
    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
    
    public Integer getStaffCount() { return staffCount; }
    public void setStaffCount(Integer staffCount) { this.staffCount = staffCount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getEntryScannedAt() { return entryScannedAt; }
    public void setEntryScannedAt(LocalDateTime entryScannedAt) { this.entryScannedAt = entryScannedAt; }
    
    public LocalDateTime getExitScannedAt() { return exitScannedAt; }
    public void setExitScannedAt(LocalDateTime exitScannedAt) { this.exitScannedAt = exitScannedAt; }
    
    public String getEntryScannedBy() { return entryScannedBy; }
    public void setEntryScannedBy(String entryScannedBy) { this.entryScannedBy = entryScannedBy; }
    
    public String getExitScannedBy() { return exitScannedBy; }
    public void setExitScannedBy(String exitScannedBy) { this.exitScannedBy = exitScannedBy; }
    
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getEntry() { return entry; }
    public void setEntry(String entry) { this.entry = entry; }
    
    public String getExit() { return exit; }
    public void setExit(String exit) { this.exit = exit; }
    
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    
    public String getGroupType() { return groupType; }
    public void setGroupType(String groupType) { this.groupType = groupType; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Helper methods for lifecycle management
    public boolean hasEntry() {
        return this.entry != null && !this.entry.isEmpty();
    }
    
    public boolean hasExit() {
        return this.exit != null && !this.exit.isEmpty();
    }
    
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }
    
    public void markEntry() {
        this.entry = "ENTERED";
        this.entryScannedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markExit() {
        this.exit = "EXITED";
        this.exitScannedAt = LocalDateTime.now();
        this.status = "COMPLETED";
        this.updatedAt = LocalDateTime.now();
    }
}
