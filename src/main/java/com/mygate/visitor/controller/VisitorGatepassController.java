package com.mygate.visitor.controller;

import com.mygate.visitor.entity.Visitor;
import com.mygate.visitor.service.VisitorGatepassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class VisitorGatepassController {
    
    @Autowired
    private VisitorGatepassService gatepassService;
    
    /**
     * STEP 2: Visitor Request Handling (Device A)
     * POST /api/visitor/request
     * Creates a new visitor gatepass request
     */
    @PostMapping("/visitor/request")
    public ResponseEntity<?> createVisitorRequest(@RequestBody VisitorRequestDTO requestDTO) {
        try {
            // Validate required fields
            if (requestDTO.getVisitorName() == null || requestDTO.getVisitorName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Visitor name is required"));
            }
            if (requestDTO.getVisitorPhone() == null || requestDTO.getVisitorPhone().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Visitor phone is required"));
            }
            if (requestDTO.getDepartment() == null || requestDTO.getDepartment().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Department is required"));
            }
            if (requestDTO.getAssignedStaffId() == null || requestDTO.getAssignedStaffId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Assigned staff ID is required"));
            }
            
            // Create entity from DTO
            Visitor request = new Visitor();
            request.setName(requestDTO.getVisitorName());
            request.setPhone(requestDTO.getVisitorPhone());
            request.setEmail(requestDTO.getVisitorEmail());
            request.setDepartment(requestDTO.getDepartment());
            request.setStaffCode(requestDTO.getAssignedStaffId());
            request.setPersonToMeet(requestDTO.getAssignedStaffId());
            request.setPurpose(requestDTO.getPurpose() != null ? requestDTO.getPurpose() : requestDTO.getReason());
            request.setNumberOfPeople(1);
            
            // Parse visit date and time if provided
            if (requestDTO.getVisitDate() != null && !requestDTO.getVisitDate().trim().isEmpty()) {
                try {
                    request.setVisitDate(LocalDate.parse(requestDTO.getVisitDate()));
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Invalid visit date format. Use YYYY-MM-DD"));
                }
            }
            
            if (requestDTO.getVisitTime() != null && !requestDTO.getVisitTime().trim().isEmpty()) {
                try {
                    request.setVisitTime(LocalTime.parse(requestDTO.getVisitTime()));
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Invalid visit time format. Use HH:MM:SS"));
                }
            }
            
            // Create request with validation
            Visitor savedRequest = gatepassService.createRequest(request);
            
            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Visitor request created successfully and sent to staff for approval");
            response.put("requestId", savedRequest.getId());
            response.put("visitorName", savedRequest.getName());
            response.put("assignedStaffId", savedRequest.getStaffCode());
            response.put("department", savedRequest.getDepartment());
            response.put("status", savedRequest.getStatus());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error creating visitor request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * STEP 3: Staff Device Visibility (Device B)
     * GET /api/staff/visitor-requests
     * Returns pending requests for logged-in staff
     */
    @GetMapping("/staff/visitor-requests")
    public ResponseEntity<?> getStaffVisitorRequests(@RequestParam String staffId) {
        try {
            if (staffId == null || staffId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Staff ID is required"));
            }
            
            // Get pending requests for this staff member
            // Always reads fresh from database - no caching
            List<Visitor> requests = gatepassService.getPendingRequestsForStaff(staffId);
            
            System.out.println("✓ Retrieved " + requests.size() + " pending requests for staff: " + staffId);
            
            return ResponseEntity.ok(requests);
            
        } catch (Exception e) {
            System.err.println("Error fetching staff visitor requests: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch visitor requests"));
        }
    }
    
    /**
     * STEP 4: Staff Approval Flow
     * PUT /api/staff/visitor-requests/{id}/approve
     * Approves a visitor request
     */
    @PutMapping("/staff/visitor-requests/{id}/approve")
    public ResponseEntity<?> approveVisitorRequest(@PathVariable Long id) {
        try {
            Visitor approvedRequest = gatepassService.approveRequest(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Visitor request approved successfully");
            response.put("requestId", approvedRequest.getId());
            response.put("visitorName", approvedRequest.getName());
            response.put("qrCode", approvedRequest.getQrCode());
            response.put("approvedAt", approvedRequest.getApprovedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error approving visitor request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * STEP 4: Staff Rejection Flow
     * PUT /api/staff/visitor-requests/{id}/reject
     * Rejects a visitor request
     */
    @PutMapping("/staff/visitor-requests/{id}/reject")
    public ResponseEntity<?> rejectVisitorRequest(
            @PathVariable Long id,
            @RequestBody RejectionDTO rejectionDTO) {
        try {
            String reason = rejectionDTO.getRejectionReason();
            if (reason == null || reason.trim().isEmpty()) {
                reason = "No reason provided";
            }
            
            Visitor rejectedRequest = gatepassService.rejectRequest(id, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Visitor request rejected");
            response.put("requestId", rejectedRequest.getId());
            response.put("visitorName", rejectedRequest.getName());
            response.put("rejectionReason", rejectedRequest.getRejectionReason());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error rejecting visitor request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Get a specific request by ID
     */
    @GetMapping("/visitor/request/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable Long id) {
        try {
            Optional<Visitor> request = gatepassService.getRequestById(id);
            if (request.isPresent()) {
                return ResponseEntity.ok(request.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Request not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch request"));
        }
    }
    
    /**
     * Get all requests for a staff member (all statuses)
     */
    @GetMapping("/staff/visitor-requests/all")
    public ResponseEntity<?> getAllStaffRequests(@RequestParam String staffId) {
        try {
            if (staffId == null || staffId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Staff ID is required"));
            }
            
            List<Visitor> requests = gatepassService.getAllRequestsForStaff(staffId);
            return ResponseEntity.ok(requests);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch requests"));
        }
    }
    
    // Helper method to create error response
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        return error;
    }
    
    // DTO Classes
    public static class VisitorRequestDTO {
        private String visitorName;
        private String visitorPhone;
        private String visitorEmail;
        private String department;
        private String assignedStaffId;
        private String purpose;
        private String reason;
        private String visitDate;  // Format: YYYY-MM-DD
        private String visitTime;  // Format: HH:MM:SS
        
        // Getters and Setters
        public String getVisitorName() { return visitorName; }
        public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
        
        public String getVisitorPhone() { return visitorPhone; }
        public void setVisitorPhone(String visitorPhone) { this.visitorPhone = visitorPhone; }
        
        public String getVisitorEmail() { return visitorEmail; }
        public void setVisitorEmail(String visitorEmail) { this.visitorEmail = visitorEmail; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public String getAssignedStaffId() { return assignedStaffId; }
        public void setAssignedStaffId(String assignedStaffId) { this.assignedStaffId = assignedStaffId; }
        
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public String getVisitDate() { return visitDate; }
        public void setVisitDate(String visitDate) { this.visitDate = visitDate; }
        
        public String getVisitTime() { return visitTime; }
        public void setVisitTime(String visitTime) { this.visitTime = visitTime; }
    }
    
    public static class RejectionDTO {
        private String rejectionReason;
        
        public String getRejectionReason() { return rejectionReason; }
        public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    }
}
