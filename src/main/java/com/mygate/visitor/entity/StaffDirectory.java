package com.mygate.visitor.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "staff_directory")
public class StaffDirectory {
    
    @Id
    @Column(name = "staff_id", length = 50)
    private String staffId;
    
    @Column(name = "staff_name", length = 120)
    private String staffName;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(name = "designation", length = 100)
    private String designation;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    // Constructors
    public StaffDirectory() {}
    
    public StaffDirectory(String staffId, String staffName) {
        this.staffId = staffId;
        this.staffName = staffName;
    }
    
    // Getters and Setters
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
