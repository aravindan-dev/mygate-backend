package com.mygate.visitor.controller;

import com.mygate.visitor.entity.GatePassRequest;
import com.mygate.visitor.entity.Student;
import com.mygate.visitor.entity.Staff;
import com.mygate.visitor.entity.HOD;
import com.mygate.visitor.entity.HODBulkGatePassRequest;
import com.mygate.visitor.repository.StudentRepository;
import com.mygate.visitor.repository.StaffRepository;
import com.mygate.visitor.repository.HODRepository;
import com.mygate.visitor.repository.GatePassRequestRepository;
import com.mygate.visitor.service.GatePassRequestService;
import com.mygate.visitor.service.HODBulkGatePassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hod")
@RequiredArgsConstructor
@Slf4j
public class HODController {
    
    private final GatePassRequestService gatePassRequestService;
    private final HODBulkGatePassService hodBulkGatePassService;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final HODRepository hodRepository;
    private final GatePassRequestRepository gatePassRequestRepository;
    
    // ==================== HOD GATE PASS REQUESTS ====================
    
    // Submit HOD gate pass request
    @PostMapping("/gate-pass/submit")
    public ResponseEntity<?> submitHODGatePassRequest(@RequestBody Map<String, Object> requestData) {
        try {
            String hodCode = (String) requestData.get("hodCode");
            String purpose = (String) requestData.get("purpose");
            String reason = (String) requestData.get("reason");
            String attachmentUri = (String) requestData.get("attachmentUri");
            
            if (hodCode == null || purpose == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", "HOD code and purpose are required"
                ));
            }
            
            GatePassRequest request = gatePassRequestService.submitHODRequest(
                hodCode, purpose, reason, LocalDateTime.now(), attachmentUri);
            
            log.info("✅ HOD gate pass request submitted: {}", request.getId());
            
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Gate pass request submitted successfully",
                "requestId", request.getId(),
                "request", request
            ));
            
        } catch (Exception e) {
            log.error("Error submitting HOD gate pass request", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "ERROR",
                "message", "Failed to submit request: " + e.getMessage()
            ));
        }
    }
    
    // Get HOD's own gate pass requests
    @GetMapping("/gate-pass/my-requests")
    public ResponseEntity<?> getMyRequests(@RequestParam String hodCode) {
        try {
            List<GatePassRequest> requests = gatePassRequestService.getRequestsByHOD(hodCode);
            
            log.info("Fetched {} requests for HOD {}", requests.size(), hodCode);
            
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "requests", requests,
                "count", requests.size()
            ));
            
        } catch (Exception e) {
            log.error("Error fetching HOD requests", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "ERROR",
                "message", "Failed to fetch requests: " + e.getMessage()
            ));
        }
    }
    
    // Get requests pending HOD approval (for student/staff requests)
    @GetMapping("/gate-pass/pending")
    public ResponseEntity<?> getPendingRequests(@RequestParam String hodCode) {
        try {
            List<GatePassRequest> requests = gatePassRequestService.getRequestsForHodApproval(hodCode);
            
            log.info("Fetched {} pending requests for HOD {}", requests.size(), hodCode);
            
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "requests", requests,
                "count", requests.size()
            ));
            
        } catch (Exception e) {
            log.error("Error fetching pending requests for HOD", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "ERROR",
                "message", "Failed to fetch pending requests: " + e.getMessage()
            ));
        }
    }
    
    // Get all requests for HOD approval
    @GetMapping("/gate-pass/all")
    public ResponseEntity<?> getAllRequests(@RequestParam String hodCode) {
        try {
            List<GatePassRequest> requests = gatePassRequestService.getAllRequestsForHod(hodCode);
            
            log.info("Fetched {} total requests for HOD {}", requests.size(), hodCode);
            
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "requests", requests,
                "count", requests.size()
            ));
            
        } catch (Exception e) {
            log.error("Error fetching all requests for HOD", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "ERROR",
                "message", "Failed to fetch requests: " + e.getMessage()
            ));
        }
    }
    
    // Approve request as HOD
    @PostMapping("/gate-pass/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id, @RequestBody Map<String, String> data) {
        try {
            String hodCode = data.get("hodCode");
            
            if (hodCode == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", "HOD code is required"
                ));
            }
            
            GatePassRequest request = gatePassRequestService.approveByHOD(id, hodCode);
            
            log.info("✅ Request {} approved by HOD {}", id, hodCode);
            
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Request approved successfully",
                "request", request
            ));
            
        } catch (Exception e) {
            log.error("Error approving request", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "ERROR",
                "message", "Failed to approve request: " + e.getMessage()
            ));
        }
    }
    
    // Reject request as HOD
    @PostMapping("/gate-pass/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, @RequestBody Map<String, String> data) {
        try {
            String hodCode = data.get("hodCode");
            String reason = data.get("reason");
            
            if (hodCode == null || reason == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", "HOD code and reason are required"
                ));
            }
            
            GatePassRequest request = gatePassRequestService.rejectByHOD(id, hodCode, reason);
            
            log.info("✅ Request {} rejected by HOD {}", id, hodCode);
            
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Request rejected successfully",
                "request", request
            ));
            
        } catch (Exception e) {
            log.error("Error rejecting request", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "ERROR",
                "message", "Failed to reject request: " + e.getMessage()
            ));
        }
    }
    
    // Get pending count
    @GetMapping("/gate-pass/pending-count")
    public ResponseEntity<?> getPendingCount(@RequestParam String hodCode) {
        try {
            long count = gatePassRequestService.getPendingRequestsCountForHod(hodCode);
            
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "count", count
            ));
            
        } catch (Exception e) {
            log.error("Error fetching pending count", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "ERROR",
                "message", "Failed to fetch pending count: " + e.getMessage()
            ));
        }
    }
    
    // Get QR code for approved request
    @GetMapping("/gate-pass/{id}/qr-code")
    public ResponseEntity<?> getQRCode(@PathVariable Long id, @RequestParam String hodCode) {
        try {
            String qrCode = gatePassRequestService.getQRCodeForRequest(id, hodCode);
            
            // Also fetch the manual code
            Optional<GatePassRequest> requestOpt = gatePassRequestRepository.findById(id);
            String manualCode = requestOpt.map(GatePassRequest::getManualCode).orElse(null);
            
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "qrCode", qrCode,
                "manualCode", manualCode != null ? manualCode : ""
            ));
            
        } catch (Exception e) {
            log.error("Error fetching QR code", e);
            return ResponseEntity.status(403).body(Map.of(
                "status", "ERROR",
                "message", e.getMessage()
            ));
        }
    }
    
    // ==================== HOD BULK GATE PASS ENDPOINTS ====================
    
    // Get students from HOD's department with optional year filter
    @GetMapping("/{hodCode}/department/students")
    public ResponseEntity<?> getDepartmentStudents(
            @PathVariable String hodCode,
            @RequestParam(required = false) Integer year) {
        try {
            // Get HOD to find their department
            HOD hod = hodRepository.findByHodCode(hodCode)
                .orElseThrow(() -> new RuntimeException("HOD not found"));
            
            // Get all students from the department
            List<Student> allStudents = studentRepository.findAll().stream()
                .filter(s -> s.getDepartment() != null && 
                           s.getDepartment().equalsIgnoreCase(hod.getDepartment()) &&
                           s.getIsActive())
                .collect(Collectors.toList());
            
            // Filter by year if provided
            if (year != null) {
                allStudents = allStudents.stream()
                    .filter(s -> s.getYear() != null && s.getYear().startsWith(year.toString()))
                    .collect(Collectors.toList());
            }
            
            // Map to response format
            List<Map<String, Object>> students = allStudents.stream()
                .map(s -> {
                    Map<String, Object> studentMap = new HashMap<>();
                    studentMap.put("id", s.getId());
                    studentMap.put("regNo", s.getRegNo());
                    studentMap.put("fullName", s.getFullName());
                    studentMap.put("department", s.getDepartment());
                    studentMap.put("year", parseYear(s.getYear()));
                    return studentMap;
                })
                .collect(Collectors.toList());
            
            log.info("Fetched {} students for HOD {} (department: {}, year: {})", 
                students.size(), hodCode, hod.getDepartment(), year);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "students", students,
                "count", students.size()
            ));
            
        } catch (Exception e) {
            log.error("Error fetching department students", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Failed to fetch students: " + e.getMessage()
            ));
        }
    }
    
    // Get staff from HOD's department
    @GetMapping("/{hodCode}/department/staff")
    public ResponseEntity<?> getDepartmentStaff(@PathVariable String hodCode) {
        try {
            // Get HOD to find their department
            HOD hod = hodRepository.findByHodCode(hodCode)
                .orElseThrow(() -> new RuntimeException("HOD not found"));
            
            // Get all staff from the department
            List<Staff> allStaff = staffRepository.findAll().stream()
                .filter(s -> s.getDepartment() != null && 
                           s.getDepartment().equalsIgnoreCase(hod.getDepartment()) &&
                           s.getIsActive())
                .collect(Collectors.toList());
            
            // Map to response format
            List<Map<String, Object>> staff = allStaff.stream()
                .map(s -> {
                    Map<String, Object> staffMap = new HashMap<>();
                    staffMap.put("id", s.getId());
                    staffMap.put("staffCode", s.getStaffCode());
                    staffMap.put("fullName", s.getStaffName());
                    staffMap.put("department", s.getDepartment());
                    return staffMap;
                })
                .collect(Collectors.toList());
            
            log.info("Fetched {} staff for HOD {} (department: {})", 
                staff.size(), hodCode, hod.getDepartment());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "staff", staff,
                "count", staff.size()
            ));
            
        } catch (Exception e) {
            log.error("Error fetching department staff", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Failed to fetch staff: " + e.getMessage()
            ));
        }
    }
    
    // Submit HOD bulk gate pass request (unified Gatepass table)
    // Alias endpoint for frontend compatibility
    @PostMapping("/{hodCode}/bulk-gate-pass")
    public ResponseEntity<?> submitBulkGatePassAlias(@PathVariable String hodCode, @RequestBody Map<String, Object> requestData) {
        // Add hodCode to request data if not present
        if (!requestData.containsKey("hodCode")) {
            requestData.put("hodCode", hodCode);
        }
        return submitBulkGatePass(requestData);
    }
    
    // Submit HOD bulk gate pass request (unified Gatepass table)
    @PostMapping("/bulk-pass/create")
    public ResponseEntity<?> submitBulkGatePass(@RequestBody Map<String, Object> requestData) {
        try {
            String hodCode = (String) requestData.get("hodCode");
            
            // Validate HOD exists
            HOD hod = hodRepository.findByHodCode(hodCode)
                .orElseThrow(() -> new RuntimeException("HOD not found"));
            
            // Extract parameters
            List<String> studentRegNos = (List<String>) requestData.get("studentRegNos");
            List<String> staffCodes = (List<String>) requestData.get("staffCodes");
            String purpose = (String) requestData.get("purpose");
            String reason = (String) requestData.get("reason");
            String exitDateTimeStr = (String) requestData.get("exitDateTime");
            String returnDateTimeStr = (String) requestData.get("returnDateTime");
            Boolean includeHOD = (Boolean) requestData.get("includeHOD");
            String receiverId = (String) requestData.get("receiverId");
            String attachmentUri = (String) requestData.get("attachmentUri");
            
            // Parse dates
            LocalDateTime exitDateTime = LocalDateTime.parse(exitDateTimeStr);
            LocalDateTime returnDateTime = LocalDateTime.parse(returnDateTimeStr);
            
            // Create bulk pass request using unified service
            Map<String, Object> response = hodBulkGatePassService.createBulkGatePassRequest(
                hodCode, studentRegNos, staffCodes, purpose, reason,
                exitDateTime, returnDateTime, includeHOD, receiverId, attachmentUri
            );
            
            if ((Boolean) response.get("success")) {
                log.info("✅ HOD bulk gate pass request created: ID={}, HOD={}, participants={}", 
                    response.get("requestId"), hodCode, response.get("participantCount"));
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error submitting HOD bulk gate pass", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Failed to submit bulk gate pass: " + e.getMessage()
            ));
        }
    }
    
    // Helper method to parse year from string
    private Integer parseYear(String yearStr) {
        if (yearStr == null || yearStr.isEmpty()) {
            return null;
        }
        try {
            // Extract first digit from year string (e.g., "1st Year" -> 1)
            String numStr = yearStr.replaceAll("[^0-9]", "");
            if (!numStr.isEmpty()) {
                return Integer.parseInt(numStr.substring(0, 1));
            }
        } catch (Exception e) {
            log.warn("Failed to parse year: {}", yearStr);
        }
        return null;
    }
    
    // ==================== HOD BULK PASS MANAGEMENT ====================
    
    // Get HOD's bulk pass requests (unified Gatepass table)
    @GetMapping("/{hodCode}/bulk-pass/requests")
        public ResponseEntity<?> getHODBulkPassRequests(@PathVariable String hodCode) {
            try {
                List<GatePassRequest> requests = hodBulkGatePassService.getHODRequests(hodCode);

                List<Map<String, Object>> requestList = requests.stream().map(req -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", req.getId());
                    map.put("purpose", req.getPurpose());
                    map.put("reason", req.getReason());
                    map.put("exitDateTime", req.getExitDateTime());
                    map.put("returnDateTime", req.getReturnDateTime());

                    // Calculate participant count from student_list and staff_list
                    int participantCount = 0;

                    // Count students from student_list
                    if (req.getStudentList() != null && !req.getStudentList().trim().isEmpty()) {
                        String[] students = req.getStudentList().split(",");
                        participantCount += students.length;
                    }

                    // Count staff from staff_list
                    if (req.getStaffList() != null && !req.getStaffList().trim().isEmpty()) {
                        String[] staff = req.getStaffList().split(",");
                        participantCount += staff.length;
                    }

                    map.put("participantCount", participantCount);
                    map.put("includeHOD", req.getIncludeStaff()); // Using includeStaff field
                    map.put("status", req.getStatus());
                    map.put("hrApproval", req.getHrApproval());
                    map.put("qrCode", req.getQrCode());
                    map.put("createdAt", req.getCreatedAt());
                    if (req.getHrApprovedBy() != null) {
                        map.put("approvedByHr", req.getHrApprovedBy());
                        map.put("approvalDate", req.getHrApprovalDate());
                    }
                    if (req.getRejectionReason() != null) {
                        map.put("rejectionReason", req.getRejectionReason());
                    }
                    return map;
                }).collect(Collectors.toList());

                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "requests", requestList,
                    "count", requestList.size()
                ));
            } catch (Exception e) {
                log.error("Error fetching HOD bulk pass requests", e);
                return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error fetching bulk pass requests: " + e.getMessage()
                ));
            }
        }
    
    // Get specific bulk pass request details (unified Gatepass table)
    @GetMapping("/bulk-pass/details/{requestId}")
    public ResponseEntity<?> getBulkPassRequestDetails(@PathVariable Long requestId) {
        try {
            Map<String, Object> response = hodBulkGatePassService.getBulkGatePassDetails(requestId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching bulk pass details", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error fetching bulk pass details: " + e.getMessage()
            ));
        }
    }

    // Get HOD profile by HOD code
    @GetMapping("/{hodCode}")
    public ResponseEntity<?> getHODProfile(@PathVariable String hodCode) {
        try {
            log.info("📋 Fetching HOD profile for: {}", hodCode);

            Optional<HOD> hodOpt = hodRepository.findByHodCode(hodCode);
            if (!hodOpt.isPresent()) {
                log.error("❌ HOD not found: {}", hodCode);
                return ResponseEntity.notFound().build();
            }

            HOD hod = hodOpt.get();
            Map<String, Object> hodDTO = new HashMap<>();
            hodDTO.put("id", hod.getId());
            hodDTO.put("hodCode", hod.getHodCode());
            hodDTO.put("hodId", hod.getHodCode());
            hodDTO.put("name", hod.getHodName());
            hodDTO.put("hodName", hod.getHodName());
            hodDTO.put("email", hod.getEmail());
            hodDTO.put("phone", hod.getPhone());
            hodDTO.put("department", hod.getDepartment());
            hodDTO.put("isActive", hod.getIsActive());

            log.info("✅ Found HOD: {}", hod.getHodName());
            return ResponseEntity.ok(hodDTO);

        } catch (Exception e) {
            log.error("❌ Error fetching HOD profile: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
