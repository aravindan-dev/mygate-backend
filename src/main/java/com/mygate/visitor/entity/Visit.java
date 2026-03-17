package com.mygate.visitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "visits")
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer numberOfPeople;
    
    @ElementCollection
    @CollectionTable(name = "visitor_names", joinColumns = @JoinColumn(name = "visit_id"))
    @Column(name = "name")
    private List<String> visitorNames;
    
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    
    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;
    
    @Column(nullable = false, length = 500)
    private String purposeOfVisit;
    
    @Column
    private String vehicleNumber;
    
    @Column(nullable = false)
    private LocalDateTime visitDateTime;
    
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED
    
    @Column
    private String qrCode;
    
    public Visit() {
        this.visitDateTime = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getNumberOfPeople() { return numberOfPeople; }
    public void setNumberOfPeople(Integer numberOfPeople) { this.numberOfPeople = numberOfPeople; }
    
    public List<String> getVisitorNames() { return visitorNames; }
    public void setVisitorNames(List<String> visitorNames) { this.visitorNames = visitorNames; }
    
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    
    public Staff getStaff() { return staff; }
    public void setStaff(Staff staff) { this.staff = staff; }
    
    public String getPurposeOfVisit() { return purposeOfVisit; }
    public void setPurposeOfVisit(String purposeOfVisit) { this.purposeOfVisit = purposeOfVisit; }
    
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    
    public LocalDateTime getVisitDateTime() { return visitDateTime; }
    public void setVisitDateTime(LocalDateTime visitDateTime) { this.visitDateTime = visitDateTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
}