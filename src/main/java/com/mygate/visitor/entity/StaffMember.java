package com.mygate.visitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
public class StaffMember {
    @Id
    @Column(name = "staff_code", nullable = false, unique = true, length = 50)
    private String staffCode;
    
    @Column(name = "staff_name", nullable = false, length = 200)
    private String staffName;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 100)
    private String department;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    // Getters and Setters
    public String getStaffCode() { return staffCode; }
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }
    
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    
    // Convenience method for getName() - returns staffName
    public String getName() { return staffName; }
    public void setName(String name) { this.staffName = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    // Convenience method for getRole() - returns department as role
    public String getRole() { return department; }
    public void setRole(String role) { this.department = role; }
    
    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
