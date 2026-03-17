package com.mygate.visitor.controller;

import com.mygate.visitor.entity.GatePassRequest;
import com.mygate.visitor.entity.Staff;
import com.mygate.visitor.repository.GatePassRequestRepository;
import com.mygate.visitor.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    
    @Autowired
    private StaffRepository staffRepository;
    
    @Autowired
    private com.mygate.visitor.repository.StudentRepository studentRepository;
    
    @Autowired
    private com.mygate.visitor.service.BulkGatePassService bulkGatePassService;
    
    @Autowired
    private GatePassRequestRepository gatePassRequestRepository;
    
    // Get all staff
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllStaff() {
        try {
            List<Staff> staffList = staffRepository.findAll();
            List<Map<String, Object>> staffDTOs = staffList.stream()
                .filter(staff -> staff.getIsActive())
                .map(staff -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", staff.getStaffCode());
                    map.put("staffId", staff.getStaffCode());
                    map.put("name", staff.getStaffName());
                    map.put("role", staff.getDepartment()); // Using department as role for now
                    map.put("phone", staff.getPhone());
                    map.put("email", staff.getEmail());
                    map.put("department", staff.getDepartment());
                    return map;
                })
                .collect(Collectors.toList());
            
            System.out.println("Fetching " + staffDTOs.size() + " staff members from database");
            return ResponseEntity.ok(staffDTOs);
        } catch (Exception e) {
            System.err.println("Error fetching all staff: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get staff by staff code
    @GetMapping("/{staffCode}")
    public ResponseEntity<Map<String, Object>> getStaffByCode(@PathVariable String staffCode) {
        try {
            System.out.println("📋 Fetching staff profile for: " + staffCode);
            
            java.util.Optional<Staff> staffOpt = staffRepository.findByStaffCode(staffCode);
            if (staffOpt.isEmpty()) {
                System.err.println("❌ Staff not found: " + staffCode);
                return ResponseEntity.notFound().build();
            }
            
            Staff staff = staffOpt.get();
            Map<String, Object> staffDTO = new HashMap<>();
            staffDTO.put("id", staff.getStaffCode());
            staffDTO.put("staffCode", staff.getStaffCode());
            staffDTO.put("staffId", staff.getStaffCode());
            staffDTO.put("name", staff.getStaffName());
            staffDTO.put("staffName", staff.getStaffName());
            staffDTO.put("email", staff.getEmail());
            staffDTO.put("phone", staff.getPhone());
            staffDTO.put("department", staff.getDepartment());
            staffDTO.put("isActive", staff.getIsActive());
            
            System.out.println("✅ Found staff: " + staff.getStaffName());
            return ResponseEntity.ok(staffDTO);
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching staff profile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get staff by department code
    @GetMapping("/department/{departmentCode}")
    public ResponseEntity<List<Map<String, Object>>> getStaffByDepartmentCode(@PathVariable String departmentCode) {
        try {
            List<Staff> staffList = staffRepository.findByDepartment(departmentCode);
            List<Map<String, Object>> staffDTOs = staffList.stream()
                .filter(staff -> staff.getIsActive())
                .map(staff -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", staff.getStaffCode());
                    map.put("staffId", staff.getStaffCode());
                    map.put("name", staff.getStaffName());
                    map.put("role", staff.getDepartment()); // Using department as role for now
                    map.put("phone", staff.getPhone());
                    map.put("email", staff.getEmail());
                    map.put("department", staff.getDepartment());
                    return map;
                })
                .collect(Collectors.toList());
            
            System.out.println("Fetching " + staffDTOs.size() + " staff members for department: " + departmentCode);
            return ResponseEntity.ok(staffDTOs);
        } catch (Exception e) {
            System.err.println("Error fetching staff by department: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get students for staff's department (for bulk gate pass)
    @GetMapping("/{staffCode}/students")
    public ResponseEntity<?> getStudentsByStaffDepartment(@PathVariable String staffCode) {
        try {
            System.out.println("📋 Fetching students for staff: " + staffCode);
            
            // Find the staff member
            java.util.Optional<Staff> staffOpt = staffRepository.findByStaffCode(staffCode);
            if (staffOpt.isEmpty()) {
                System.err.println("❌ Staff not found: " + staffCode);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Staff not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            Staff staff = staffOpt.get();
            String department = staff.getDepartment();
            System.out.println("📚 Staff department: " + department);
            
            // Get all students from the same department
            List<com.mygate.visitor.entity.Student> students = studentRepository.findByDepartment(department);
            System.out.println("✅ Found " + students.size() + " students in department: " + department);
            
            // Convert to DTOs
            List<Map<String, Object>> studentDTOs = students.stream()
                .filter(student -> student.getIsActive())
                .map(student -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", student.getId());
                    map.put("regNo", student.getRegNo());
                    map.put("name", student.getFullName());
                    map.put("firstName", student.getFirstName());
                    map.put("lastName", student.getLastName());
                    map.put("email", student.getEmail());
                    map.put("phone", student.getPhone());
                    map.put("department", student.getDepartment());
                    map.put("year", student.getYear());
                    return map;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("students", studentDTOs);
            response.put("department", department);
            response.put("count", studentDTOs.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching students for staff: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error fetching students: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // ============================================
    // BULK GATE PASS ENDPOINTS
    // ============================================
    
    @PostMapping("/bulk-gatepass/create")
    public ResponseEntity<?> createBulkGatePass(@RequestBody Map<String, Object> request) {
        try {
            String staffCode = (String) request.get("staffCode");
            String purpose = (String) request.get("purpose");
            String reason = (String) request.get("reason");
            String attachmentUri = (String) request.get("attachmentUri");
            @SuppressWarnings("unchecked")
            List<String> students = (List<String>) request.get("students");
            Boolean includeStaff = (Boolean) request.get("includeStaff");
            String receiverId = (String) request.get("receiverId");
            
            // Parse exit and return date times
            java.time.LocalDateTime exitDateTime = null;
            java.time.LocalDateTime returnDateTime = null;
            
            try {
                String exitDateTimeStr = (String) request.get("exitDateTime");
                if (exitDateTimeStr != null && !exitDateTimeStr.isEmpty()) {
                    exitDateTime = java.time.LocalDateTime.parse(exitDateTimeStr, java.time.format.DateTimeFormatter.ISO_DATE_TIME);
                }
            } catch (Exception e) {
                System.err.println("⚠️  Error parsing exitDateTime: " + e.getMessage());
            }
            
            try {
                String returnDateTimeStr = (String) request.get("returnDateTime");
                if (returnDateTimeStr != null && !returnDateTimeStr.isEmpty()) {
                    returnDateTime = java.time.LocalDateTime.parse(returnDateTimeStr, java.time.format.DateTimeFormatter.ISO_DATE_TIME);
                }
            } catch (Exception e) {
                System.err.println("⚠️  Error parsing returnDateTime: " + e.getMessage());
            }
            
            // Use BulkGatePassService to create request (will set status to PENDING_HOD)
            Map<String, Object> response = bulkGatePassService.createBulkGatePassRequest(
                staffCode, students, purpose, reason, exitDateTime, returnDateTime, includeStaff, receiverId, attachmentUri);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error creating bulk gate pass: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(createErrorResponse("Failed to create bulk gate pass"));
        }
    }
    
    @GetMapping("/bulk-gatepass/{requestId}")
    public ResponseEntity<?> getBulkGatePassDetails(@PathVariable Long requestId) {
        try {
            // Use BulkGatePassService to get details
            Map<String, Object> response = bulkGatePassService.getBulkGatePassDetails(requestId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching bulk gate pass details: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(createErrorResponse("Failed to fetch bulk gate pass details"));
        }
    }
    
    @GetMapping("/bulk-gatepass/students/{staffCode}")
    public ResponseEntity<?> getStudentsByDepartment(@PathVariable String staffCode) {
        try {
            // Use BulkGatePassService to get students
            Map<String, Object> response = bulkGatePassService.getStudentsByStaffDepartment(staffCode);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching students: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(createErrorResponse("Failed to fetch students"));
        }
    }
    
    /**
     * Get all bulk pass requests created by a staff member
     * GET /api/staff/{staffCode}/bulk-pass/requests
     */
    @GetMapping("/{staffCode}/bulk-pass/requests")
    public ResponseEntity<?> getStaffBulkPassRequests(@PathVariable String staffCode) {
        try {
            System.out.println("📋 Fetching bulk pass requests for staff: " + staffCode);
            
            // Get all bulk pass requests where this staff is the creator
            List<GatePassRequest> requests = gatePassRequestRepository
                .findByRequestedByStaffCodeAndPassTypeOrderByCreatedAtDesc(staffCode, "BULK");
            
            System.out.println("✅ Found " + requests.size() + " bulk pass requests for staff " + staffCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("requests", requests);
            response.put("count", requests.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching staff bulk pass requests: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(createErrorResponse("Failed to fetch bulk pass requests"));
        }
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
