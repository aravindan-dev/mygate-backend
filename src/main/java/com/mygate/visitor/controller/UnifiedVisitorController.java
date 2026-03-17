package com.mygate.visitor.controller;

import com.mygate.visitor.entity.GatePassRequest;
import com.mygate.visitor.service.UnifiedVisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Unified Visitor Controller - Uses only gate_pass_requests table
 * Replaces old VisitorController that used separate visitors table
 */
@RestController
@RequestMapping("/api/unified-visitors")
public class UnifiedVisitorController {
    
    @Autowired
    private UnifiedVisitorService unifiedVisitorService;
    
    /**
     * Register visitor from website
     * POST /api/unified-visitors/register
     */
    @PostMapping("/register")
    public ResponseEntity<VisitorRegistrationResponse> registerVisitor(
            @RequestBody VisitorRegistrationRequest request) {
        try {
            System.out.println("📝 Registering visitor: " + request.getName());
            
            GatePassRequest gatePassRequest = unifiedVisitorService.createVisitorRequest(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getDepartment(),
                request.getStaffCode(),
                request.getPurpose(),
                request.getReason(),
                request.getNumberOfPeople(),
                request.getVehicleNumber()
            );
            
            VisitorRegistrationResponse response = new VisitorRegistrationResponse();
            response.setId(gatePassRequest.getId());
            response.setName(gatePassRequest.getStudentName());
            response.setEmail(gatePassRequest.getRegNo());
            response.setDepartment(gatePassRequest.getDepartment());
            response.setPersonToMeet(request.getStaffCode());
            response.setApprovalStatus("PENDING");
            response.setMessage("Your visit request has been sent for approval. You will receive an email with your QR code once approved.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error registering visitor: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Register visitor from security dashboard
     * POST /api/unified-visitors/security-register
     */
    @PostMapping("/security-register")
    public ResponseEntity<VisitorRegistrationResponse> registerVisitorBySecurity(
            @RequestBody SecurityVisitorRegistrationRequest request) {
        try {
            System.out.println("🔐 Security registering visitor: " + request.getName());
            
            GatePassRequest gatePassRequest = unifiedVisitorService.createVisitorRequestBySecurity(
                request.getSecurityId(),
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getDepartmentId(),
                request.getStaffCode(),
                request.getPurpose(),
                request.getNumberOfPeople(),
                request.getVehicleNumber()
            );
            
            VisitorRegistrationResponse response = new VisitorRegistrationResponse();
            response.setId(gatePassRequest.getId());
            response.setName(gatePassRequest.getStudentName());
            response.setEmail(gatePassRequest.getRegNo());
            response.setDepartment(gatePassRequest.getDepartment());
            response.setPersonToMeet(request.getStaffCode());
            response.setApprovalStatus("PENDING");
            response.setMessage("Visitor registered successfully. Request sent for approval.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error registering visitor by security: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get visitor requests for staff member
     * GET /api/unified-visitors/staff/{staffCode}/requests
     */
    @GetMapping("/staff/{staffCode}/requests")
    public ResponseEntity<List<VisitorRequestDTO>> getVisitorRequestsForStaff(
            @PathVariable String staffCode,
            @RequestParam(required = false) String status) {
        try {
            System.out.println("📡 Fetching visitor requests for staff: " + staffCode);
            
            List<GatePassRequest> requests;
            if ("PENDING".equalsIgnoreCase(status)) {
                requests = unifiedVisitorService.getPendingVisitorRequestsForStaff(staffCode);
            } else {
                requests = unifiedVisitorService.getAllVisitorRequestsForStaff(staffCode);
            }
            
            List<VisitorRequestDTO> dtos = requests.stream()
                    .map(this::convertToDTO)
                    .toList();
            
            System.out.println("✅ Found " + dtos.size() + " visitor requests");
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("❌ Error fetching visitor requests: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Approve visitor request
     * POST /api/unified-visitors/{id}/approve
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<VisitorApprovalResponse> approveVisitorRequest(
            @PathVariable Long id,
            @RequestParam String approvedBy) {
        try {
            System.out.println("✅ Approving visitor request: " + id + " by " + approvedBy);
            
            GatePassRequest approved = unifiedVisitorService.approveVisitorRequest(id, approvedBy);
            
            VisitorApprovalResponse response = new VisitorApprovalResponse();
            response.setId(approved.getId());
            response.setStatus("APPROVED");
            response.setQrCode(approved.getQrCode());
            response.setManualCode(approved.getManualCode());
            response.setMessage("Visitor request approved successfully. QR code sent to visitor's email.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error approving visitor request: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Reject visitor request
     * POST /api/unified-visitors/{id}/reject
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectVisitorRequest(
            @PathVariable Long id,
            @RequestBody RejectionRequest request) {
        try {
            System.out.println("❌ Rejecting visitor request: " + id);
            
            unifiedVisitorService.rejectVisitorRequest(
                id, 
                request.getStaffCode(),
                request.getReason() != null ? request.getReason() : "Rejected by staff"
            );
            
            return ResponseEntity.ok("Visitor request rejected successfully");
        } catch (Exception e) {
            System.err.println("❌ Error rejecting visitor request: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get visitor request by ID
     * GET /api/unified-visitors/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GatePassRequest> getVisitorRequestById(@PathVariable Long id) {
        try {
            return unifiedVisitorService.getVisitorRequestById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("❌ Error fetching visitor request: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Helper method to convert GatePassRequest to DTO
    private VisitorRequestDTO convertToDTO(GatePassRequest request) {
        VisitorRequestDTO dto = new VisitorRequestDTO();
        dto.setRequestId(request.getId());
        dto.setRequestType("VISITOR");
        dto.setRequesterName(request.getStudentName());
        dto.setVisitorEmail(request.getRegNo());
        dto.setVisitorPhone(extractPhoneFromReason(request.getReason()));
        dto.setPurpose(request.getPurpose());
        dto.setDepartment(request.getDepartment());
        dto.setPersonToMeet(request.getAssignedStaffCode());
        dto.setStatus(request.getStatus().toString());
        dto.setCreatedAt(request.getRequestDate());
        dto.setApprovedAt(request.getStaffApprovalDate());
        dto.setQrCode(request.getQrCode());
        dto.setManualCode(request.getManualCode());
        return dto;
    }
    
    private String extractPhoneFromReason(String reason) {
        if (reason == null) return null;
        String[] lines = reason.split("\n");
        for (String line : lines) {
            if (line.startsWith("Phone: ")) {
                return line.substring(7);
            }
        }
        return null;
    }
    
    // Request/Response DTOs
    public static class VisitorRegistrationRequest {
        private String name;
        private String email;
        private String phone;
        private String department;
        private String purpose;
        private String reason;
        private Integer numberOfPeople;
        private String vehicleNumber;
        private String personToMeet;
        private String staffCode;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public Integer getNumberOfPeople() { return numberOfPeople; }
        public void setNumberOfPeople(Integer numberOfPeople) { this.numberOfPeople = numberOfPeople; }
        
        public String getVehicleNumber() { return vehicleNumber; }
        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
        
        public String getPersonToMeet() { return personToMeet; }
        public void setPersonToMeet(String personToMeet) { this.personToMeet = personToMeet; }
        
        public String getStaffCode() { return staffCode; }
        public void setStaffCode(String staffCode) { this.staffCode = staffCode; }
    }
    
    public static class SecurityVisitorRegistrationRequest {
        private String securityId;
        private String name;
        private String email;
        private String phone;
        private String departmentId;
        private String staffCode;
        private String purpose;
        private Integer numberOfPeople;
        private String vehicleNumber;
        
        // Getters and Setters
        public String getSecurityId() { return securityId; }
        public void setSecurityId(String securityId) { this.securityId = securityId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getDepartmentId() { return departmentId; }
        public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
        
        public String getStaffCode() { return staffCode; }
        public void setStaffCode(String staffCode) { this.staffCode = staffCode; }
        
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
        
        public Integer getNumberOfPeople() { return numberOfPeople; }
        public void setNumberOfPeople(Integer numberOfPeople) { this.numberOfPeople = numberOfPeople; }
        
        public String getVehicleNumber() { return vehicleNumber; }
        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    }
    
    public static class VisitorRegistrationResponse {
        private Long id;
        private String name;
        private String email;
        private String department;
        private String personToMeet;
        private String approvalStatus;
        private String message;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public String getPersonToMeet() { return personToMeet; }
        public void setPersonToMeet(String personToMeet) { this.personToMeet = personToMeet; }
        
        public String getApprovalStatus() { return approvalStatus; }
        public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    public static class VisitorRequestDTO {
        private Long requestId;
        private String requestType;
        private String requesterName;
        private String visitorEmail;
        private String visitorPhone;
        private String purpose;
        private String department;
        private String personToMeet;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime approvedAt;
        private String qrCode;
        private String manualCode;
        
        // Getters and Setters
        public Long getRequestId() { return requestId; }
        public void setRequestId(Long requestId) { this.requestId = requestId; }
        
        public String getRequestType() { return requestType; }
        public void setRequestType(String requestType) { this.requestType = requestType; }
        
        public String getRequesterName() { return requesterName; }
        public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
        
        public String getVisitorEmail() { return visitorEmail; }
        public void setVisitorEmail(String visitorEmail) { this.visitorEmail = visitorEmail; }
        
        public String getVisitorPhone() { return visitorPhone; }
        public void setVisitorPhone(String visitorPhone) { this.visitorPhone = visitorPhone; }
        
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public String getPersonToMeet() { return personToMeet; }
        public void setPersonToMeet(String personToMeet) { this.personToMeet = personToMeet; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getApprovedAt() { return approvedAt; }
        public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
        
        public String getQrCode() { return qrCode; }
        public void setQrCode(String qrCode) { this.qrCode = qrCode; }
        
        public String getManualCode() { return manualCode; }
        public void setManualCode(String manualCode) { this.manualCode = manualCode; }
    }
    
    public static class VisitorApprovalResponse {
        private Long id;
        private String status;
        private String qrCode;
        private String manualCode;
        private String message;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getQrCode() { return qrCode; }
        public void setQrCode(String qrCode) { this.qrCode = qrCode; }
        
        public String getManualCode() { return manualCode; }
        public void setManualCode(String manualCode) { this.manualCode = manualCode; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    public static class RejectionRequest {
        private String staffCode;
        private String reason;
        
        // Getters and Setters
        public String getStaffCode() { return staffCode; }
        public void setStaffCode(String staffCode) { this.staffCode = staffCode; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
