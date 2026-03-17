package com.mygate.visitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Entry")
public class ScanLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String qrCode;
    
    private String personName;
    
    @Enumerated(EnumType.STRING)
    private PersonType personType;
    
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;
    
    private String department;
    private String email;
    private String phone;
    private String studentId;
    private String facultyId;
    private String designation;
    private String purpose;
    
    @Column(name = "scanned_by")
    private String scannedBy; // Security personnel who scanned
    
    @Column(name = "scan_location")
    private String scanLocation; // Gate/Entry point
    
    @Column(name = "access_granted")
    private boolean accessGranted;
    
    @Column(name = "scan_time")
    private LocalDateTime scanTime;
    
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;
    
    @Column(name = "user_type", nullable = false, length = 20)
    private String userType;
    
    @Column(name = "qr_id")
    private Long qrId;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    // Constructors
    public ScanLog() {
        this.scanTime = LocalDateTime.now();
        this.timestamp = LocalDateTime.now();
        this.personType = PersonType.VISITOR; // Default for unknown QR codes
        this.status = ApprovalStatus.REJECTED; // Default for unknown QR codes
        this.accessGranted = false;
        this.userId = "UNKNOWN"; // Default value to satisfy NOT NULL constraint
        this.userType = "VISITOR"; // Default value to satisfy NOT NULL constraint
    }
    
    public ScanLog(String qrCode, String personName, PersonType personType, ApprovalStatus status) {
        this();
        this.qrCode = qrCode;
        this.personName = personName;
        this.personType = personType;
        this.status = status;
        this.accessGranted = (status == ApprovalStatus.APPROVED);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    
    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }
    
    public PersonType getPersonType() { return personType; }
    public void setPersonType(PersonType personType) { this.personType = personType; }
    
    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { 
        this.status = status;
        this.accessGranted = (status == ApprovalStatus.APPROVED);
    }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }
    
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    
    public String getScannedBy() { return scannedBy; }
    public void setScannedBy(String scannedBy) { this.scannedBy = scannedBy; }
    
    public String getScanLocation() { return scanLocation; }
    public void setScanLocation(String scanLocation) { this.scanLocation = scanLocation; }
    
    public boolean getAccessGranted() { return accessGranted; }
    public void setAccessGranted(boolean accessGranted) { this.accessGranted = accessGranted; }
    
    public LocalDateTime getScanTime() { return scanTime; }
    public void setScanTime(LocalDateTime scanTime) { this.scanTime = scanTime; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    
    public Long getQrId() { return qrId; }
    public void setQrId(Long qrId) { this.qrId = qrId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}