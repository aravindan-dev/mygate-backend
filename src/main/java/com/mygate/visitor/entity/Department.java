package com.mygate.visitor.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Department")
public class Department {
    @Id
    @Column(name = "department_code", nullable = false)
    private String code;
    
    @Column(name = "department_name", nullable = false)
    private String name;
    
    @Column(name = "hod_name")
    private String hodName;
    
    @Column(name = "hod_staff_code")
    private String hodStaffCode;
    
    @Column(name = "building")
    private String building;
    
    @Column(name = "floor")
    private String floor;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "total_staff")
    private Integer totalStaff;
    
    @Column(name = "total_students")
    private Integer totalStudents;
    
    @Column(name = "is_active")
    private boolean isActive;
    
    public Department() {}
    
    public Department(String code, String name) {
        this.code = code;
        this.name = name;
        this.isActive = true;
    }
    
    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    // Alias methods for compatibility
    public String getDepartmentCode() { return code; }
    public String getDepartmentName() { return name; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getHodName() { return hodName; }
    public void setHodName(String hodName) { this.hodName = hodName; }
    
    public String getHodStaffCode() { return hodStaffCode; }
    public void setHodStaffCode(String hodStaffCode) { this.hodStaffCode = hodStaffCode; }
    
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    
    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }
    
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getTotalStaff() { return totalStaff; }
    public void setTotalStaff(Integer totalStaff) { this.totalStaff = totalStaff; }
    
    public Integer getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Integer totalStudents) { this.totalStudents = totalStudents; }
    
    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
}